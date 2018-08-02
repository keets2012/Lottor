package com.blueskykong.lottor.sc.config;

import com.blueskykong.lottor.core.config.TransactionCoreAutoConfiguration;
import com.blueskykong.lottor.core.interceptor.TxTransactionInterceptor;
import com.blueskykong.lottor.core.service.AspectTransactionService;
import com.blueskykong.lottor.core.service.ModelNameService;
import com.blueskykong.lottor.sc.interceptor.SpringCloudTxTransactionInterceptor;
import com.blueskykong.lottor.sc.service.SpringCloudModelNameServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(value = "tx.enabled", matchIfMissing = false)
@AutoConfigureAfter({TransactionCoreAutoConfiguration.class})
public class SpringCloudAutoConfiguration {

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
}
