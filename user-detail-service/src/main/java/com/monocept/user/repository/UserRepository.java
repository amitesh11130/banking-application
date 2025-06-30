package com.monocept.user.repository;

import com.monocept.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserName(String userName);

    boolean existsByUserNameOrAadharNumber(String userName, String aadharNumber);

    User findByAccountNumber(String accountNumber);
}

