package com.monocept.transaction.controller;

import com.monocept.transaction.entity.TransactionHistory;
import com.monocept.transaction.request.TransactionDto;
import com.monocept.transaction.response.ApiResponse;
import com.monocept.transaction.response.Meta;
import com.monocept.transaction.response.Status;
import com.monocept.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/createTransaction")
    public ApiResponse createTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("Received request to create transaction for account: {}", transactionDto.getAccountNumber());

        TransactionHistory transaction = transactionService.createTransaction(transactionDto);

        Meta meta = Meta.builder()
                .code(HttpStatus.CREATED.value())
                .status(Status.SUCCESS)
                .description("Transaction saved successfully")
                .build();

        log.info("Transaction created for account: {}, type: {}, amount: {}",
                transaction.getAccountNumber(), transaction.getType(), transaction.getAmount());

        return ApiResponse.builder().meta(meta).data(transaction).build();
    }

    @GetMapping("/getAllTransaction")
    public ApiResponse fetchAllTransaction(@RequestParam String accountNumber) {
        log.info("Received request to fetch transactions for account: {}", accountNumber);

        List<TransactionHistory> allTransaction = transactionService.getAllTransaction(accountNumber);

        Meta meta = Meta.builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .description("Transactions fetched successfully")
                .build();

        log.info("Returning {} transactions for account: {}", allTransaction.size(), accountNumber);

        return ApiResponse.builder().meta(meta).data(allTransaction).build();
    }
}

