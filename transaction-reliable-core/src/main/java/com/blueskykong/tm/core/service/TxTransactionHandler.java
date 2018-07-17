
package com.blueskykong.tm.core.service;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author keets
 */
@FunctionalInterface
public interface TxTransactionHandler {

    /**
     * 分布式事务处理接口
     *
     * @param info 信息
     * @return Object
     * @throws Throwable 异常
     */
    Object handler(TxTransactionInfo info);
}
