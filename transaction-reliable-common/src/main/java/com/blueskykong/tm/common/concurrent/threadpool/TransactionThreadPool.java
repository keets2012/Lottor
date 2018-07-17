package com.blueskykong.tm.common.concurrent.threadpool;


import com.blueskykong.tm.common.concurrent.threadpool.policy.AbortPolicy;
import com.blueskykong.tm.common.concurrent.threadpool.policy.BlockingPolicy;
import com.blueskykong.tm.common.concurrent.threadpool.policy.CallerRunsPolicy;
import com.blueskykong.tm.common.concurrent.threadpool.policy.DiscardedPolicy;
import com.blueskykong.tm.common.concurrent.threadpool.policy.RejectedPolicy;
import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.enums.BlockingQueueTypeEnum;
import com.blueskykong.tm.common.enums.RejectedPolicyTypeEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public class TransactionThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionThreadPool.class);

    private static final String THREAD_FACTORY_NAME = "txTransaction";
    private static final int MAX_ARRAY_QUEUE = 1000;

    private TxConfig txConfig;

    private ScheduledExecutorService scheduledExecutorService;

    private ExecutorService fixExecutorService;

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            new ScheduledThreadPoolExecutor(1,
                    TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false));


    @PostConstruct
    public void init() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(txConfig.getTransactionThreadMax(),
                TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false));

        fixExecutorService = new ThreadPoolExecutor(txConfig.getTransactionThreadMax(), txConfig.getTransactionThreadMax(), 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(),
                TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false), createPolicy());

    }


    public TransactionThreadPool(TxConfig txConfig) {
        this.txConfig = txConfig;
    }


    private RejectedExecutionHandler createPolicy() {
        RejectedPolicyTypeEnum rejectedPolicyType = RejectedPolicyTypeEnum.fromString(txConfig.getRejectPolicy());

        switch (rejectedPolicyType) {
            case BLOCKING_POLICY:
                return new BlockingPolicy();
            case CALLER_RUNS_POLICY:
                return new CallerRunsPolicy();
            case ABORT_POLICY:
                return new AbortPolicy();
            case REJECTED_POLICY:
                return new RejectedPolicy();
            case DISCARDED_POLICY:
                return new DiscardedPolicy();
            default:
                return new RejectedPolicy();
        }
    }

    private BlockingQueue<Runnable> createBlockingQueue() {
        BlockingQueueTypeEnum queueType = BlockingQueueTypeEnum.fromString(txConfig.getBlockingQueueType());

        switch (queueType) {
            case LINKED_BLOCKING_QUEUE:
                return new LinkedBlockingQueue<>(1024);
            case ARRAY_BLOCKING_QUEUE:
                return new ArrayBlockingQueue<>(MAX_ARRAY_QUEUE);
            case SYNCHRONOUS_QUEUE:
                return new SynchronousQueue<>();
            default:
                return new LinkedBlockingQueue<>(1024);
        }

    }

    public ExecutorService newCustomFixedThreadPool(int threads) {
        return new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(),
                TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false), createPolicy());
    }

    public ExecutorService newFixedThreadPool() {
        return fixExecutorService;
    }

    public ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                createBlockingQueue(),
                TxTransactionThreadFactory.create(THREAD_FACTORY_NAME, false), createPolicy());
    }

    public ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return SCHEDULED_EXECUTOR_SERVICE;
    }

    public ScheduledExecutorService newScheduledThreadPool() {
        return scheduledExecutorService;
    }


    public ScheduledFuture multiScheduled(Supplier<Object> supplier) {
        return scheduledExecutorService
                .schedule(() -> {

                            final Boolean o = (Boolean) supplier.get();
                            if (o) {
                                LogUtil.info(LOGGER, "多线程执行任务调度成功,调度时间为:{}", txConfig::getDelayTime);
                            } else {
                                LogUtil.info(LOGGER, "多线程执行任务调度未执行任务,调度时间为:{}", txConfig::getDelayTime);
                            }
                        },
                        txConfig.getDelayTime(), TimeUnit.SECONDS);
    }

    public ScheduledFuture multiScheduled(Supplier<Object> supplier, int waitTime) {
        return scheduledExecutorService
                .schedule(() -> {
                            final Boolean o = (Boolean) supplier.get();
                            if (o) {
                                LogUtil.info(LOGGER, "多线程执行任务调度成功,调度时间为:{}", txConfig::getDelayTime);
                            } else {
                                LogUtil.info(LOGGER, "多线程执行任务调度未执行任务,调度时间为:{}", txConfig::getDelayTime);
                            }
                        },
                        waitTime, TimeUnit.SECONDS);
    }

    public ScheduledFuture singleThreadScheduled(Supplier<Object> supplier) {
        return SCHEDULED_EXECUTOR_SERVICE
                .schedule(() -> {
                            final Boolean o = (Boolean) supplier.get();
                            if (o) {
                                LogUtil.info(LOGGER, "单线程执行任务调度成功,调度时间为:{}", txConfig::getDelayTime);
                            } else {
                                LogUtil.info(LOGGER, "单线程执行任务调度未执行任务,调度时间为:{}", txConfig::getDelayTime);
                            }
                        },
                        txConfig.getDelayTime(), TimeUnit.SECONDS);
    }

}

