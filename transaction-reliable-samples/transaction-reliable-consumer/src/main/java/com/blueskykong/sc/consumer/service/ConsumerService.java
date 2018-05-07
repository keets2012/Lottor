package com.blueskykong.sc.consumer.service;

import com.blueskykong.sc.consumer.domain.Product;
import com.blueskykong.tm.common.holder.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author keets
 * @data 2018/5/7.
 */
@Service
public class ConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    public void testConsumer(Product product) {
        // some operations for consumer
        LogUtil.info(LOGGER, "{} consume the tx msg!", product::getClass);
    }

}
