
package com.blueskykong.tm.core.service;


import com.blueskykong.tm.common.bean.TxTransactionInfo;

/**
 * @author keets
 */
@FunctionalInterface
public interface TxTransactionFactoryService<T> {

    /**
     * 返回 实现TxTransactionHandler类的名称
     *
     * @param info
     * @return Class<T>
     * @throws Throwable 抛出异常
     */
    Class<T> factoryOf(TxTransactionInfo info);
}
