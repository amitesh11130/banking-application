package com.monocept;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class BankingServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingServiceRegistryApplication.class, args);
    }

}
