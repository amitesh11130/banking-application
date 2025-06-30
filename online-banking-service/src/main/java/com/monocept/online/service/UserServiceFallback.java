package com.monocept.online.service;

import com.monocept.online.dto.ApiResponse;
import com.monocept.online.dto.Meta;
import com.monocept.online.dto.Status;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserServiceFallback implements UserServiceClient {
    @Override
    public ApiResponse getUserDetails(String userName) {
        Meta meta = Meta.builder().code(HttpStatus.SERVICE_UNAVAILABLE.value()).status(Status.UNAVAILABLE).description("Service Unavailable").build();
        return ApiResponse.builder()
                .meta(meta)
                .data(null)
                .build();
    }

    @Override
    public Boolean checkUserIsActiveOrNot(String userName) {
        return null;
    }

    @Override
    public ApiResponse withdrawBalance(String accountNumber, Double amount) {
        return null;
    }

    @Override
    public ApiResponse depositBalance(String accountNumber, Double amount) {
        return null;
    }

    @Override
    public String activeAccountNumber(String toAccountNumber) {
        return null;
    }

    @Override
    public Boolean checkBalance(String fromAccountNumber, Double balance) {
        return null;
    }

    @Override
    public String transferAmount(String toAccountNumber, String fromAccountNumber, Double balance) {
        return "";
    }

}

