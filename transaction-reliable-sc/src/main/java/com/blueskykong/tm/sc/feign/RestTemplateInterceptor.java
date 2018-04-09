package com.blueskykong.tm.sc.feign;

import com.blueskykong.tm.common.constant.CommonConstant;
import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionLocal;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * @author keets
 */
public class RestTemplateInterceptor implements RequestInterceptor {
    //TODO 增加自动补偿的机制

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(CommonConstant.TX_TRANSACTION_GROUP, TxTransactionLocal.getInstance().getTxGroupId());
    }

}
