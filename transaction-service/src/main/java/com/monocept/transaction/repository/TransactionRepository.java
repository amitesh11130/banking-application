package com.monocept.transaction.repository;

import com.monocept.transaction.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionHistory, Long> {

    List<TransactionHistory> findByAccountNumber(String accountNumber);
}
