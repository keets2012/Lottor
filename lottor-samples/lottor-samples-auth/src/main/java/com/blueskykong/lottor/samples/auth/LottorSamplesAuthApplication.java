package com.blueskykong.lottor.samples.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LottorSamplesAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottorSamplesAuthApplication.class, args);
    }
}
