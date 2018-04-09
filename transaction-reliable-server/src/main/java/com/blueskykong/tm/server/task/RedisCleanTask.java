package com.blueskykong.tm.server.task;

import com.blueskykong.tm.server.service.TxManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
public class RedisCleanTask {

    private final TxManagerService txManagerService;


    @Autowired
    public RedisCleanTask(TxManagerService txManagerService) {
        this.txManagerService = txManagerService;
    }


    /**
     * 清除完全提交的事务组信息，每隔5分钟执行一次
     *
     * @throws InterruptedException 异常
     */
    public void removeCommitTxGroup() throws InterruptedException {
        txManagerService.removeCommitTxGroup();

    }


    /**
     * 清除完全回滚的事务组信息，每隔10分钟执行一次
     *
     * @throws InterruptedException 异常
     */
    public void removeRollBackTxGroup() throws InterruptedException {
        txManagerService.removeRollBackTxGroup();
    }
}
