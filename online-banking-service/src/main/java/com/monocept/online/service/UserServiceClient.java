package com.monocept.online.service;

import com.monocept.online.config.FeignClientConfig;
import com.monocept.online.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-detail-service",
        path = "/api/v1/user",
        fallback = UserServiceFallback.class,
        configuration = FeignClientConfig.class)

public interface UserServiceClient {

    @GetMapping("/details")
    ApiResponse getUserDetails(@RequestParam("userName") String userName);

    @GetMapping("/active")
    Boolean checkUserIsActiveOrNot(@RequestParam("userName") String userName);

    @GetMapping("/withdraw")
    ApiResponse withdrawBalance(@RequestParam String accountNumber, @RequestParam Double balance);

    @PostMapping("/deposit")
    ApiResponse depositBalance(@RequestParam String accountNumber, @RequestParam Double balance);

    @GetMapping("/activeAccountNumber")
    String activeAccountNumber(@RequestParam String accountNumber);

    @GetMapping("/balanceAvailable")
    Boolean checkBalance(@RequestParam String accountNumber, @RequestParam Double balance);

    @PostMapping("/transferMoney")
    String transferAmount(@RequestParam String toAccountNumber, @RequestParam String fromAccountNumber, @RequestParam Double balance);
}


