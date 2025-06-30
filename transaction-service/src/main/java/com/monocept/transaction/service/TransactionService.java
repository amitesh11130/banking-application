package com.monocept.transaction.service;

import com.monocept.transaction.entity.TransactionHistory;
import com.monocept.transaction.repository.TransactionRepository;
import com.monocept.transaction.request.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;


    public TransactionHistory createTransaction(TransactionDto transactionDto) {
        log.info("Creating transaction for account: {}, type: {}, amount: {}",
                transactionDto.getAccountNumber(), transactionDto.getType(), transactionDto.getAmount());

        TransactionHistory transactionHistory = convertDtoToEntity(transactionDto);
        TransactionHistory savedTransaction = transactionRepository.save(transactionHistory);

        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());

        return savedTransaction;
    }

    private TransactionHistory convertDtoToEntity(TransactionDto transactionDto) {
        return TransactionHistory.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .type(transactionDto.getType())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public List<TransactionHistory> getAllTransaction(String accountNumber) {
        log.info("Fetching all transactions for account: {}", accountNumber);
        List<TransactionHistory> transactions = transactionRepository.findByAccountNumber(accountNumber);
        log.info("Found {} transaction(s) for account: {}", transactions.size(), accountNumber);
        return transactions;
    }
}
