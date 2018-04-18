package com.blueskykong.tm.core.netty;

import com.blueskykong.tm.common.config.TxConfig;

/**
 * @author keets
 */
public interface NettyClientService {


    /**
     * 启动netty客户端
     *
     * @param txConfig 配置信息
     */
    void start(TxConfig txConfig);

    /**
     * 停止服务
     */
    void stop();


    /**
     * 连接netty服务
     */
    void doConnect();

    /**
     * 重启
     */
    void restart();


    /**
     * 检查状态
     *
     * @return TRUE 正常
     */
    boolean checkState();
}
