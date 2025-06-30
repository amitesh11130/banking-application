//package com.monocept.online.service;
//
//import feign.Feign;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
//import org.springframework.context.annotation.Bean;
//
//@LoadBalancerClient("user-detail-service")
//public class AdhSerLoadBalancer {
//
//    @LoadBalanced
//    @Bean
//    public Feign.Builder feign() {
//        return Feign.builder();
//    }
//}
