package com.blueskykong.lottor.core.netty.impl;

import com.blueskykong.lottor.common.config.TxConfig;
import com.blueskykong.lottor.common.entity.TxManagerServer;
import com.blueskykong.lottor.common.enums.SerializeProtocolEnum;
import com.blueskykong.lottor.common.exception.TransactionRuntimeException;
import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.core.feign.ManagerClient;
import com.blueskykong.lottor.core.netty.NettyClientService;
import com.blueskykong.lottor.core.netty.handler.NettyClientHandlerInitializer;
import com.blueskykong.lottor.core.netty.handler.NettyClientMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NettyClientServiceImpl implements NettyClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientServiceImpl.class);

    private TxConfig txConfig;

    private EventLoopGroup workerGroup;

    private DefaultEventExecutorGroup servletExecutor;

    private String host = "127.0.0.1";

    private Integer port = 8888;

    private Channel channel;

    private Bootstrap bootstrap;

    private int retryMax;

    private int retryCount = 0;

    private int retryInterval;

    private final NettyClientHandlerInitializer nettyClientHandlerInitializer;

    private final ManagerClient managerClient;

    public NettyClientServiceImpl(NettyClientHandlerInitializer nettyClientHandlerInitializer, ManagerClient managerClient) {

        this.managerClient = managerClient;
        this.nettyClientHandlerInitializer = nettyClientHandlerInitializer;
    }


    /**
     * 启动netty客户端
     */
    @Override
    public void start(TxConfig txConfig) {
        this.retryMax = txConfig.getRetryMax();
        this.retryInterval = txConfig.getRetryInterval();
        this.txConfig = txConfig;
        SerializeProtocolEnum serializeProtocol =
                SerializeProtocolEnum.acquireSerializeProtocol(txConfig.getNettySerializer());
        nettyClientHandlerInitializer.setSerializeProtocolEnum(serializeProtocol);
        servletExecutor = new DefaultEventExecutorGroup(txConfig.getNettyThreadMax());
        nettyClientHandlerInitializer.setServletExecutor(servletExecutor);
        nettyClientHandlerInitializer.setTxConfig(txConfig);
        try {
            bootstrap = new Bootstrap();
            groups(bootstrap, txConfig);
            doConnect();
        } catch (Exception e) {
            LogUtil.error(LOGGER, "tx client start failed for {}", () -> e.getLocalizedMessage());
            throw new TransactionRuntimeException(e);
        }
    }

    private void groups(Bootstrap bootstrap, TxConfig txConfig) {
        int workThreads = txConfig.getNettyThreadMax();
        //暂时不使用EpollEventLoopGroup，Ubuntu报错
        if (Epoll.isAvailable() && txConfig.isOnEpoll()) {
            workerGroup = new EpollEventLoopGroup(workThreads);
            bootstrap.group(workerGroup);
            bootstrap.channel(EpollSocketChannel.class);
            bootstrap.option(EpollChannelOption.TCP_CORK, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, true)
                    .option(EpollChannelOption.CONNECT_TIMEOUT_MILLIS, 5)
                    .option(EpollChannelOption.SO_BACKLOG, 1024)
                    .option(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(nettyClientHandlerInitializer);
        } else {
            workerGroup = new NioEventLoopGroup(workThreads);
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(nettyClientHandlerInitializer);
        }
    }


    @Override
    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        TxManagerServer txManagerServer = getTmServer();

        if (Objects.nonNull(txManagerServer) &&
                StringUtils.isNoneBlank(txManagerServer.getHost())
                && Objects.nonNull(txManagerServer.getPort())) {
            host = txManagerServer.getHost();
            port = txManagerServer.getPort();
        }

        ChannelFuture future = bootstrap.connect(host, port);
        LogUtil.info(LOGGER, "连接txManager-socket服务-> {}", () -> host + ":" + port);

        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                LogUtil.info(LOGGER, "Connect to -> {} server successfully!", () -> host + ":" + port);
            } else {
                if (retryCount++ >= retryMax) {
                    LogUtil.error(LOGGER, "Failed to connect to server {}, after {} times", () -> host + ":" + port, () -> --retryCount);
                    System.exit(1);
                }
                LogUtil.error(LOGGER, "Failed to connect to server, retry for {} times, try connect after {}s-> {}", () -> retryCount, () -> this.retryInterval, () -> host + ":" + port);
                futureListener.channel().eventLoop().schedule(this::doConnect, retryInterval, TimeUnit.SECONDS);
            }
        });

    }

    //TODO 重试机制待优化
    //    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 3000L, multiplier = 1))
    private TxManagerServer getTmServer() {
        TxManagerServer txManagerServer = null;

        for (int i = 0; i < 6; i++) {
            try {
                txManagerServer = managerClient.findTxManagerServers();

            } catch (Exception e) {
                LOGGER.error("{}th retry for getting txManagerServer", i + 1);
            }
            if (Objects.nonNull(txManagerServer)) {
                return txManagerServer;
            }
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return txManagerServer;
    }


    /**
     * 停止服务
     */
    @Override
    public void stop() {
        if (Objects.nonNull(servletExecutor)) {
            workerGroup.shutdownGracefully();
        }
        if (Objects.nonNull(servletExecutor)) {
            servletExecutor.shutdownGracefully();
        }

    }

    /**
     * 重启
     */
    @Override
    public void restart() {
        stop();
        start(txConfig);
    }


    /**
     * 检查状态
     *
     * @return TRUE 正常
     */
    @Override
    public boolean checkState() {
        if (!NettyClientMessageHandler.net_state) {
            LogUtil.error(LOGGER, () -> "socket服务尚未建立连接成功,将在此等待2秒.");
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!NettyClientMessageHandler.net_state) {
                LogUtil.error(LOGGER, () -> "TxManager还未连接成功,请检查TxManager服务后再试");
                return false;
            }
        }

        return true;
    }
}
