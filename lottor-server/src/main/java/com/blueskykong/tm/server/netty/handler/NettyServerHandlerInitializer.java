package com.blueskykong.tm.server.netty.handler;

import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.netty.NettyPipelineInit;
import com.blueskykong.tm.server.config.NettyConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyConfig nettyConfig;


    private final NettyServerMessageHandler nettyServerMessageHandler;

    private SerializeProtocolEnum serializeProtocolEnum;


    private DefaultEventExecutorGroup servletExecutor;

    public void setServletExecutor(DefaultEventExecutorGroup servletExecutor) {
        this.servletExecutor = servletExecutor;
    }

    @Autowired
    public NettyServerHandlerInitializer(NettyConfig nettyConfig, NettyServerMessageHandler nettyServerMessageHandler) {
        this.nettyConfig = nettyConfig;
        this.nettyServerMessageHandler = nettyServerMessageHandler;
    }

    public void setSerializeProtocolEnum(SerializeProtocolEnum serializeProtocolEnum) {
        this.serializeProtocolEnum = serializeProtocolEnum;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        NettyPipelineInit.serializePipeline(serializeProtocolEnum, pipeline);
        pipeline.addLast("timeout",
                new IdleStateHandler(nettyConfig.getHeartTime(), nettyConfig.getHeartTime(), nettyConfig.getHeartTime(), TimeUnit.SECONDS));
        pipeline.addLast(nettyServerMessageHandler);
    }
}
