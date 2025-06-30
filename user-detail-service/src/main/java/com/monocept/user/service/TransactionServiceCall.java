package com.monocept.user.service;

import com.monocept.user.config.FeignClientConfig;
import com.monocept.user.request.TransactionDto;
import com.monocept.user.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "transaction-service",
        path = "/api/v1/transaction",
        fallback = TransactionServiceFallback.class,
        configuration = FeignClientConfig.class)

public interface TransactionServiceCall {

    @PostMapping("/createTransaction")
    ApiResponse createTransaction(@RequestBody TransactionDto transactionDto);

    @GetMapping("/getAllTransaction")
    ApiResponse fetchAllTransaction(@RequestParam String accountNumber);
}
