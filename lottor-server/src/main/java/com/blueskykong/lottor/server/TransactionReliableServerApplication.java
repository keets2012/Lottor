package com.blueskykong.lottor.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableSwagger2
public class TransactionReliableServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionReliableServerApplication.class, args);
    }
}
