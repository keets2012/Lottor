package com.blueskykong.sc.producer.rest;

import com.blueskykong.sc.producer.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author keets
 * @data 2018/3/19.
 */
@RestController
public class ProducerController {

    @Autowired
    private PayService payService;

    @GetMapping("/test")
    public String test() {
        System.out.println("开始调用事务！");
//        preSends(null);
        payService.createAffair();
        return "this is ok!";
    }
}
