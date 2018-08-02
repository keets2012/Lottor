package com.blueskykong.lottor.core.service;


import com.blueskykong.lottor.common.config.TxConfig;

@FunctionalInterface
public interface InitService {

    /**
     * 框架的初始化
     *
     * @param txConfig 配置信息{@linkplain TxConfig}
     */
    void initialization(TxConfig txConfig);
}
