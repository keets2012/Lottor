package com.blueskykong.tm.core.compensation.impl;

import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.concurrent.threadpool.TransactionThreadPool;
import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.CompensationActionEnum;
import com.blueskykong.tm.common.helper.SpringBeanUtils;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.compensation.TxOperateService;
import com.blueskykong.tm.core.compensation.command.TxOperateAction;
import com.blueskykong.tm.core.service.ModelNameService;
import com.blueskykong.tm.core.spi.TransactionOperateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class TxOperateServiceImpl implements TxOperateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxOperateServiceImpl.class);

    private TransactionOperateRepository transactionOperateRepository;

    private final ModelNameService modelNameService;

    private TxConfig txConfig;

    private static BlockingQueue<TxOperateAction> QUEUE;

    public TxOperateServiceImpl(ModelNameService modelNameService) {
        this.modelNameService = modelNameService;
    }

    @Override
    public void start(TxConfig txConfig) throws Exception {
        this.txConfig = txConfig;
        if (txConfig.getCompensation()) {
            final String modelName = modelNameService.findModelName();
            this.transactionOperateRepository = SpringBeanUtils.getInstance().getBean(TransactionOperateRepository.class);
            transactionOperateRepository.init(modelName, txConfig);
            initOperatePool();
        }
    }

    public void initOperatePool() {
        synchronized (LOGGER) {
            QUEUE = new LinkedBlockingQueue<>(txConfig.getCompensationQueueMax());
            final int compensationThreadMax = txConfig.getCompensationThreadMax();
            final TransactionThreadPool threadPool = SpringBeanUtils.getInstance().getBean(TransactionThreadPool.class);
            final ExecutorService executorService = threadPool.newCustomFixedThreadPool(compensationThreadMax);
            LogUtil.info(LOGGER, "启动OperatePool操作线程数量为:{}", () -> compensationThreadMax);
            for (int i = 0; i < compensationThreadMax; i++) {
                executorService.execute(new Worker());
            }
        }
    }

    /**
     * 保存事务信息
     *
     * @param transactionRecover 实体对象
     * @return 主键id
     */
    @Override
    public String saveTransactionRecover(TransactionRecover transactionRecover) {
        final int rows = transactionOperateRepository.create(transactionRecover);
        if (rows > 0) {
            return transactionRecover.getId();
        }
        return null;
    }

    /**
     * 删除事务信息
     *
     * @param id 主键id
     * @return true成功 false 失败
     */
    @Override
    public boolean removeTransactionRecover(String id) {
        final int rows = transactionOperateRepository.remove(id);
        return rows > 0;
    }

    @Override
    public TransactionRecover findTransactionRecover(String id) {
        return transactionOperateRepository.findById(id);
    }

    /**
     * 更新TransactionRecover
     *
     * @param transactionRecover 实体对象
     */
    @Override
    public void updateTransactionRecover(TransactionRecover transactionRecover) {
        transactionOperateRepository.update(transactionRecover);
    }

    /**
     * 提交操作
     *
     * @param txOperateAction 命令
     */
    @Override
    public Boolean submit(TxOperateAction txOperateAction) {
        try {
            if (txConfig.getCompensation()) {
                QUEUE.put(txOperateAction);
            }
        } catch (InterruptedException e) {
            LogUtil.error(LOGGER, "命令提交队列失败：{}", e::getMessage);
            return false;
        }
        return true;
    }

    @Override
    public String saveTransactionMsg(TransactionMsg msg) {
        final long rows = transactionOperateRepository.saveMsg(msg);
        if (rows > 0) {
            return msg.getGroupId();
        }
        return null;

    }

    @Override
    public void updateTransactionMsg(TransactionMsg msg) {
        transactionOperateRepository.updateMsg(msg);
    }

    @Override
    public TransactionMsg findTransactionMsg(String id) {
        return transactionOperateRepository.findMsgById(id);
    }


    class Worker implements Runnable {

        @Override
        public void run() {
            execute();
        }

        private void execute() {
            while (true) {
                try {
                    //得到需要回滚的事务对象
                    TxOperateAction transaction = QUEUE.take();
                    if (transaction != null) {
                        final CompensationActionEnum code = transaction.getCompensationActionEnum();
                        switch (code) {
                            case SAVE:
                                saveTransactionRecover(transaction.getTransactionRecover());
                                break;
                            case VIEW:
                                findTransactionRecover(transaction.getTransactionRecover().getId());
                                break;
                            case DELETE:
                                removeTransactionRecover(transaction.getTransactionRecover().getId());
                                break;
                            case UPDATE:
                                updateTransactionRecover(transaction.getTransactionRecover());
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.error(LOGGER, "执行命令失败：{}", e::getMessage);
                }
            }
        }
    }

}
