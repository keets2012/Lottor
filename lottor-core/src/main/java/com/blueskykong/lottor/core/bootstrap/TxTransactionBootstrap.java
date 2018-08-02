package com.blueskykong.lottor.core.bootstrap;

import com.blueskykong.lottor.common.config.TxConfig;
import com.blueskykong.lottor.common.exception.TransactionRuntimeException;
import com.blueskykong.lottor.common.helper.SpringBeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

public class TxTransactionBootstrap implements ApplicationContextAware {


    private ConfigurableApplicationContext cfgContext;

    private TxConfig txConfig;

    private final TxTransactionInitialize txTransactionInitialize;

    @Autowired
    public TxTransactionBootstrap(TxTransactionInitialize txTransactionInitialize, TxConfig txConfig) {
        this.txTransactionInitialize = txTransactionInitialize;
        this.txConfig = txConfig;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cfgContext = (ConfigurableApplicationContext) applicationContext;
        SpringBeanUtils.getInstance().setCfgContext(cfgContext);
        start(txConfig);
    }


    private void start(TxConfig txConfig) {
        if (!checkDataConfig(txConfig)) {
            throw new TransactionRuntimeException("分布式事务配置信息不完整！");
        }
        txTransactionInitialize.init(txConfig);
    }

    private boolean checkDataConfig(TxConfig txConfig) {
        return !StringUtils.isBlank(txConfig.getTxManagerId());
    }
}




























