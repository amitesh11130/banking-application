package com.monocept.user.service;

import com.monocept.user.request.TransactionDto;
import com.monocept.user.response.ApiResponse;

public class TransactionServiceFallback implements TransactionServiceCall {
    @Override
    public ApiResponse createTransaction(TransactionDto transactionDto) {
        return null;
    }

    @Override
    public ApiResponse fetchAllTransaction(String accountNumber) {
        return null;
    }
}
