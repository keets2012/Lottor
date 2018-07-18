package com.blueskykong.tm.server.netty.impl;

import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.helper.SpringBeanUtils;
import com.blueskykong.tm.common.holder.ServiceBootstrap;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.server.config.NettyConfig;
import com.blueskykong.tm.server.netty.NettyService;
import com.blueskykong.tm.server.netty.handler.NettyServerHandlerInitializer;
import com.blueskykong.tm.server.service.TxManagerService;
import com.blueskykong.tm.server.socket.SocketManager;
import com.google.common.base.StandardSystemProperty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;


@Component
@Order(0)
public class NettyServerServiceImpl implements NettyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerServiceImpl.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private DefaultEventExecutorGroup servletExecutor;
    private static int MAX_THREADS = Runtime.getRuntime().availableProcessors() << 1;

    private static final String OS_NAME = "Linux";

    private final TxManagerService txManagerService;

    private final NettyConfig nettyConfig;

    private final NettyServerHandlerInitializer nettyServerHandlerInitializer;

    @Autowired
    public NettyServerServiceImpl(TxManagerService txManagerService, NettyConfig nettyConfig, NettyServerHandlerInitializer nettyServerHandlerInitializer) {
        this.txManagerService = txManagerService;
        this.nettyConfig = nettyConfig;
        this.nettyServerHandlerInitializer = nettyServerHandlerInitializer;
    }

    /**
     * 启动netty服务
     */
    @Override
    public void start() {
        SocketManager.getInstance().setMaxConnection(nettyConfig.getMaxConnection());
        servletExecutor = new DefaultEventExecutorGroup(MAX_THREADS);
        if (nettyConfig.getMaxThreads() != 0) {
            MAX_THREADS = nettyConfig.getMaxThreads();
        }
        try {
            final SerializeProtocolEnum serializeProtocolEnum =
                    SerializeProtocolEnum.acquireSerializeProtocol(nettyConfig.getSerialize());

            nettyServerHandlerInitializer.setSerializeProtocolEnum(serializeProtocolEnum);
            nettyServerHandlerInitializer.setServletExecutor(servletExecutor);
            ServerBootstrap b = new ServerBootstrap();
            groups(b, MAX_THREADS << 1);
            b.bind(nettyConfig.getPort());

            LOGGER.info("netty service started on port: " + nettyConfig.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void groups(ServerBootstrap b, int workThreads) {
        /**
         * ubuntu系统不能很好的适配EpollEventLoopGroup，暂时不启用
         */
        if (Epoll.isAvailable() && nettyConfig.getOnEpoll()) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class)
                    .option(EpollChannelOption.TCP_CORK, true)
                    .option(EpollChannelOption.SO_KEEPALIVE, true)
                    .option(EpollChannelOption.SO_BACKLOG, 100)
                    .option(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup(workThreads);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyServerHandlerInitializer);
        }
    }


    /**
     * 关闭服务
     */
    @Override
    public void stop() {
        try {
            if (null != bossGroup) {
                bossGroup.shutdownGracefully().await();
            }
            if (null != workerGroup) {
                workerGroup.shutdownGracefully().await();
            }
            if (null != servletExecutor) {
                servletExecutor.shutdownGracefully().await();
            }
        } catch (InterruptedException e) {
            throw new TransactionRuntimeException(" Netty  Container stop interrupted", e);
        }

    }


}
