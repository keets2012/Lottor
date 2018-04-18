package com.blueskykong.tm.sc.interceptor;

import com.blueskykong.tm.core.interceptor.AbstractTxTransactionAspect;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

/**
 * @author keets
 */
@Aspect
public class SpringCloudTxTransactionAspect extends AbstractTxTransactionAspect implements Ordered {


    @Autowired
    public SpringCloudTxTransactionAspect(SpringCloudTxTransactionInterceptor springCloudTxTransactionInterceptor) {
        this.setTxTransactionInterceptor(springCloudTxTransactionInterceptor);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
