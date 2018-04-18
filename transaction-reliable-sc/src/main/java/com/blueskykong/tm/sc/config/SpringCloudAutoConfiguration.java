package com.blueskykong.tm.sc.config;

import com.blueskykong.tm.core.config.TransactionCoreAutoConfiguration;
import com.blueskykong.tm.core.interceptor.AbstractTxTransactionAspect;
import com.blueskykong.tm.core.interceptor.TxTransactionInterceptor;
import com.blueskykong.tm.core.service.AspectTransactionService;
import com.blueskykong.tm.core.service.ModelNameService;
import com.blueskykong.tm.sc.feign.RestTemplateInterceptor;
import com.blueskykong.tm.sc.interceptor.SpringCloudTxTransactionAspect;
import com.blueskykong.tm.sc.interceptor.SpringCloudTxTransactionInterceptor;
import com.blueskykong.tm.sc.service.SpringCloudModelNameServiceImpl;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * @author keets
 */
@Configuration
@ConditionalOnProperty(value = "tx.enabled", matchIfMissing = false)
@AutoConfigureAfter({TransactionCoreAutoConfiguration.class})
public class SpringCloudAutoConfiguration {

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder().requestInterceptor(new RestTemplateInterceptor());
    }


    @Bean
    @ConditionalOnMissingBean(ModelNameService.class)
    public ModelNameService modelNameService() {
        return new SpringCloudModelNameServiceImpl();
    }

    @Bean
    @ConditionalOnBean(AspectTransactionService.class)
    @Primary
    public TxTransactionInterceptor txTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        return new SpringCloudTxTransactionInterceptor(aspectTransactionService);
    }

    @Bean
    @ConditionalOnBean(TxTransactionInterceptor.class)
    public AbstractTxTransactionAspect abstractTxTransactionAspect(SpringCloudTxTransactionInterceptor springCloudTxTransactionInterceptor) {
        return new SpringCloudTxTransactionAspect(springCloudTxTransactionInterceptor);
    }
}
