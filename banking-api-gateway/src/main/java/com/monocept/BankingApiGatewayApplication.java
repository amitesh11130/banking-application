package com.monocept;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankingApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApiGatewayApplication.class, args);
	}

}
