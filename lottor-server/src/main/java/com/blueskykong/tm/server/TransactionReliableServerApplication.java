package com.blueskykong.tm.server;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableSwagger2Doc
public class TransactionReliableServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionReliableServerApplication.class, args);
    }
}
