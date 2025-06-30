package com.monocept.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaRepositories(basePackages = "com.monocept.user.repository")
@EntityScan("com.monocept.user.entity")
public class UserDetailServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserDetailServiceApplication.class);
    }
}