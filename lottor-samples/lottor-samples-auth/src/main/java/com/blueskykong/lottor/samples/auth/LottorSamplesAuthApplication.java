package com.blueskykong.lottor.samples.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableDiscoveryClient
@MapperScan("com.blueskykong.lottor.samples.auth.service.mapper")
public class LottorSamplesAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottorSamplesAuthApplication.class, args);
    }
}
