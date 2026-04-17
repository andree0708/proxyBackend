package com.proxy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProxyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProxyBackendApplication.class, args);
    }
}