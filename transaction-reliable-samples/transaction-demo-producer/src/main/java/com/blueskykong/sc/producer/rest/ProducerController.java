package com.blueskykong.sc.producer.rest;

import com.blueskykong.sc.producer.service.PayService;
import com.blueskykong.tm.common.holder.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @data 2018/3/19.
 */
@RestController
public class ProducerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private PayService payService;

    @GetMapping("/test")
    public String test() {
        LogUtil.info(LOGGER, () -> "开始调用事务！");
//        preSends(null);
        payService.createAffair();
        return "this is ok!";
    }
}
