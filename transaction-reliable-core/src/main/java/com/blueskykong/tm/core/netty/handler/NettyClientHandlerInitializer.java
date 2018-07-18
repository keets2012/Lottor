package com.blueskykong.tm.core.netty.handler;

import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.netty.NettyPipelineInit;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyClientHandlerInitializer extends ChannelInitializer<SocketChannel> {


    private final NettyClientMessageHandler nettyClientMessageHandler;

    private TxConfig txConfig;

    private SerializeProtocolEnum serializeProtocolEnum;

    private DefaultEventExecutorGroup servletExecutor;

    public void setServletExecutor(DefaultEventExecutorGroup servletExecutor) {
        this.servletExecutor = servletExecutor;
    }

    public void setTxConfig(TxConfig txConfig) {
        this.txConfig = txConfig;
        nettyClientMessageHandler.setTxConfig(txConfig);
    }

    @Autowired
    public NettyClientHandlerInitializer(NettyClientMessageHandler nettyClientMessageHandler) {
        this.nettyClientMessageHandler = nettyClientMessageHandler;
    }

    public void setSerializeProtocolEnum(SerializeProtocolEnum serializeProtocolEnum) {
        this.serializeProtocolEnum = serializeProtocolEnum;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        final ChannelPipeline pipeline = socketChannel.pipeline();
        NettyPipelineInit.serializePipeline(serializeProtocolEnum, pipeline);
        pipeline.addLast("timeout", new IdleStateHandler(txConfig.getHeartTime(), txConfig.getHeartTime(), txConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyClientMessageHandler);

    }
}
