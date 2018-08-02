package com.blueskykong.lottor.core.bootstrap;

import com.blueskykong.lottor.common.config.TxConfig;
import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.core.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TxTransactionInitialize {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxTransactionInitialize.class);

    private final InitService initService;

    @Autowired
    public TxTransactionInitialize(InitService initService) {
        this.initService = initService;
    }

    /**
     * 初始化服务
     */
    public void init(TxConfig txConfig) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.error("系统关闭")));
        try {
            initService.initialization(txConfig);
        } catch (RuntimeException ex) {
            LogUtil.error(LOGGER, "初始化异常:{}", ex::getMessage);
            //非正常关闭
            System.exit(1);
        }
    }


}
