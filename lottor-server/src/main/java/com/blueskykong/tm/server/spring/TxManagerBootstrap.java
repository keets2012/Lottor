package com.blueskykong.tm.server.spring;

import com.blueskykong.tm.server.netty.NettyService;
import com.blueskykong.tm.server.task.TxSyncTask;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class TxManagerBootstrap implements ApplicationContextAware {


    private final NettyService nettyService;

    private final TxSyncTask txSyncTask;

    @Autowired
    public TxManagerBootstrap(NettyService nettyService, TxSyncTask txSyncTask) {
        this.nettyService = nettyService;
        this.txSyncTask = txSyncTask;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            nettyService.start();
            txSyncTask.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
