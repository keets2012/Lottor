package com.blueskykong.lottor.samples.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableDiscoveryClient
@MapperScan("com.blueskykong.lottor.samples.user.service.mapper")
public class LottorSamplesUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottorSamplesUserApplication.class, args);
    }
}
