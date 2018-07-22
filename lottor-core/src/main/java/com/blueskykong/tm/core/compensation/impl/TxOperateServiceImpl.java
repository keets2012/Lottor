package com.blueskykong.tm.core.compensation.impl;

import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.concurrent.threadpool.TransactionThreadPool;
import com.blueskykong.tm.common.concurrent.threadpool.TxTransactionThreadFactory;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TxOperateServiceImpl implements TxOperateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxOperateServiceImpl.class);

    private TransactionOperateRepository transactionOperateRepository;

    private final ModelNameService modelNameService;

    private TxConfig txConfig;

//    private final TxManagerMessageService txManagerMessageService;

    private ScheduledExecutorService scheduledExecutorService;

    private static BlockingQueue<TxOperateAction> QUEUE;

    public TxOperateServiceImpl(ModelNameService modelNameService/*, TxManagerMessageService txManagerMessageService*/) {
        this.modelNameService = modelNameService;
//        this.txManagerMessageService = txManagerMessageService;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("CompensationService", true));
    }

    @Override
    public void compensate() {
        scheduledExecutorService
                .scheduleAtFixedRate(() -> {
                    LogUtil.debug(LOGGER, "compensate recover execute delayTime:{}", () -> txConfig.getCompensationRecoverTime());
                    /*final List<TransactionRecover> transactionRecovers =
                            transactionOperateRepository.listAllByDelay(acquireData());*/
                    /*if (CollectionUtils.isNotEmpty(transactionRecovers)) {
                        for (TransactionRecover transactionRecover : transactionRecovers) {
                            if (transactionRecover.getRetriedCount() > txConfig.getRetryMax()) {
                                LogUtil.error(LOGGER, "此事务超过了最大重试次数，不再进行重试：{}", () -> "");
                                continue;
                            }
                            try {
                                final int update = transactionOperateRepository.update(transactionRecover);
                                if (update > 0) {
                                    final TxTransactionGroup byTxGroupId = txManagerMessageService
                                            .findByTxGroupId(transactionRecover.getGroupId());
                                    if (Objects.nonNull(byTxGroupId) && CollectionUtils.isNotEmpty(byTxGroupId.getItemList())) {
                                        final Optional<TxTransactionItem> any = byTxGroupId.getItemList().stream()
                                                .filter(item -> Objects.equals(item.getTaskKey(), transactionRecover.getGroupId()))
                                                .findAny();
                                        if (any.isPresent()) {
                                            final int status = any.get().getStatus();
                                            //如果整个事务组状态是提交的
                                            if (TransactionStatusEnum.COMMIT.getCode() == status) {
                                                final Optional<TxTransactionItem> txTransactionItem = byTxGroupId.getItemList().stream()
                                                        .filter(item -> Objects.equals(item.getTaskKey(), transactionRecover.getTaskId()))
                                                        .findAny();
                                                if (txTransactionItem.isPresent()) {
                                                    final TxTransactionItem item = txTransactionItem.get();
                                                    //自己的状态不是提交，那么就进行补偿
                                                    if (item.getStatus() != TransactionStatusEnum.COMMIT.getCode()) {
                                                        submit(buildCompensate(transactionRecover));
                                                    }
                                                }
                                            } else {
                                                //不需要进行补偿，就删除
                                                submit(buildDel(transactionRecover));
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtil.error(LOGGER, "执行事务补偿异常:{}", e::getMessage);
                            }

                        }
                    }*/
                }, 30, txConfig.getCompensationRecoverTime(), TimeUnit.SECONDS);

    }


    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     */
    @Override
    public void start(TxConfig txConfig) throws Exception {
        this.txConfig = txConfig;
        if (txConfig.getCompensation()) {
            final String modelName = modelNameService.findModelName();
            this.transactionOperateRepository = SpringBeanUtils.getInstance().getBean(TransactionOperateRepository.class);
            transactionOperateRepository.init(modelName, txConfig);
            initCompensatePool();//初始化补偿操作的线程池
            compensate();//执行定时补偿
        }
    }

    public void initCompensatePool() {
        synchronized (LOGGER) {
            QUEUE = new LinkedBlockingQueue<>(txConfig.getCompensationQueueMax());
            final int compensationThreadMax = txConfig.getCompensationThreadMax();
            final TransactionThreadPool threadPool = SpringBeanUtils.getInstance().getBean(TransactionThreadPool.class);
            final ExecutorService executorService = threadPool.newCustomFixedThreadPool(compensationThreadMax);
            LogUtil.info(LOGGER, "启动补偿操作线程数量为:{}", () -> compensationThreadMax);
            for (int i = 0; i < compensationThreadMax; i++) {
                executorService.execute(new Worker());
            }
        }
    }

    /**
     * 保存补偿事务信息
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
     * 删除补偿事务信息
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
     * 提交补偿操作
     *
     * @param txOperateAction 补偿命令
     */
    @Override
    public Boolean submit(TxOperateAction txOperateAction) {
        try {
            if (txConfig.getCompensation()) {
                QUEUE.put(txOperateAction);
            }
        } catch (InterruptedException e) {
            LogUtil.error(LOGGER, "补偿命令提交队列失败：{}", e::getMessage);
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

    /**
     * 线程执行器
     */
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
                            case COMPENSATE:
                                compensatoryTransfer(transaction.getTransactionRecover());
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.error(LOGGER, "执行补偿命令失败：{}", e::getMessage);
                }
            }
        }
    }

    /**
     * 执行补偿
     *
     * @param transactionRecover 补偿信息
     */
    @SuppressWarnings("unchecked")
    private void compensatoryTransfer(TransactionRecover transactionRecover) {
        /*if (Objects.nonNull(transactionRecover)) {
            final TransactionInvocation transactionInvocation = transactionRecover.getTransactionInvocation();
            if (Objects.nonNull(transactionInvocation)) {
                final Class clazz = transactionInvocation.getTargetClazz();
                final String method = transactionInvocation.getMethod();
                final Object[] argumentValues = transactionInvocation.getArgumentValues();
                final Class[] argumentTypes = transactionInvocation.getArgumentTypes();
                final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
                try {
                    CompensationLocal.getInstance().setCompensationId(CommonConstant.COMPENSATE_ID);
                    MethodUtils.invokeMethod(bean, method, argumentValues, argumentTypes);
                    //通知tm自身已经完成提交 //删除本地信息
                    final Boolean success = txManagerMessageService.completeCommitTxTransaction(transactionRecover.getGroupId(),
                            transactionRecover.getTaskId(), TransactionStatusEnum.COMMIT.getCode());
                    if (success) {
                        transactionOperateRepository.remove(transactionRecover.getId());
                    }

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    LogUtil.error(LOGGER, "补偿方法反射调用失败！{}", e::getMessage);
                }

            }
        }*/

    }

    private TxOperateAction buildCompensate(TransactionRecover transactionRecover) {
        TxOperateAction compensationAction = new TxOperateAction();
        compensationAction.setCompensationActionEnum(CompensationActionEnum.COMPENSATE);
        compensationAction.setTransactionRecover(transactionRecover);
        return compensationAction;
    }

    private TxOperateAction buildDel(TransactionRecover transactionRecover) {
        TxOperateAction compensationAction = new TxOperateAction();
        compensationAction.setCompensationActionEnum(CompensationActionEnum.DELETE);
        compensationAction.setTransactionRecover(transactionRecover);
        return compensationAction;
    }

    private Date acquireData() {
        return new Date(LocalDateTime.now()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                (txConfig.getRecoverDelayTime() * 1000));

    }

}
