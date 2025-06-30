package com.monocept.user.controller;

import com.monocept.user.entity.Role;
import com.monocept.user.entity.UserCredential;
import com.monocept.user.request.UserDto;
import com.monocept.user.response.ApiResponse;
import com.monocept.user.response.Status;
import com.monocept.user.entity.User;
import com.monocept.user.response.ResponseUtil;
import com.monocept.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ApiResponse createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received request to create user: {}", userDto.getUserName());

        Map<String, Object> result = userService.createUser(userDto);

        if (result == null) {
            log.warn("Failed to create user: {} - Internal server error", userDto.getUserName());
            return ResponseUtil.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while creating the user", null);
        }
        if (result.containsKey("message")) {
            log.warn("User creation failed: {}", result.get("message"));
            return ResponseUtil.failed((String) result.get("message"), null);
        }
        UserCredential userCredential = UserCredential.builder()
                .username(userDto.getUserName())
                .password(userDto.getPassword())
                .role(Role.USER.name())
                .build();
        UserCredential saveUserCre = userService.createUserCredential(userCredential);
        if (saveUserCre != null) {
            log.info("User Credential created successful");
        }
        log.info("User created successfully: {}", userDto.getUserName());
        return ResponseUtil.success("User created successfully", result);
    }


    @PostMapping("/deposit")
    public ApiResponse depositBalance(@RequestParam String accountNumber, @RequestParam Double balance) {
        log.info("Received deposit request for user accountNumber: {} with amount: {}", accountNumber, balance);

        if (balance == null || balance <= 0) {
            String message = "Deposit amount must be greater than 0.";
            log.warn("Invalid deposit attempt: {}", message);
            return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, null);
        }
        Map<String, Object> depositResult = userService.depositBalance(accountNumber, balance);
        String message = (String) depositResult.get("message");
        Status status = message.contains("successful") ? Status.SUCCESS : Status.FAILURE;

        log.info("Deposit result: {}", message);

        if (status == Status.SUCCESS) {
            return ResponseUtil.success(message, depositResult);
        }
        return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, depositResult);
    }

    @GetMapping("/withdraw")
    public ApiResponse withdrawBalance(@RequestParam String accountNumber, @RequestParam Double balance) {
        log.info("Received withdrawal request for user accountNumber: {} with amount: {}", accountNumber, balance);

        if (balance == null || balance <= 0) {
            String message = "Withdraw amount must be greater than 0.";
            log.warn("Invalid withdraw attempt: {}", message);
            return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, null);
        }
        Map<String, Object> withdrawResult = userService.withdrawBalance(accountNumber, balance);
        String message = (String) withdrawResult.get("message");
        Status status = message.contains("successful") ? Status.SUCCESS : Status.FAILURE;

        log.info("Withdrawal result: {}", message);

        if (status == Status.SUCCESS) {
            return ResponseUtil.success(message, withdrawResult);
        }
        return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, withdrawResult);
    }

    @GetMapping("/getAll")
    public ApiResponse getAllUser() {
        log.info("Received request to fetch all user details");
        List<User> userList = userService.getAllUser();
        if (!userList.isEmpty()) {
            log.info("Successfully retrieved {} users from database", userList.size());
            return ResponseUtil.success("User details fetch successfully", userList);
        }
        log.warn("No users found in the database");
        return ResponseUtil.failure(HttpStatus.NOT_FOUND.value(), "User not found in data base", null);
    }

    @GetMapping("/details")
    public ApiResponse getUserDetails(@RequestParam String userName) {
        log.info("Fetching user details for: {}", userName);

        User user = userService.getUserByUserName(userName);
        if (user != null) {
            Object o = userService.transactionHistory(user.getAccountNumber());
            log.info("User found: {} - Returning transaction history", userName);
            Map<String, Object> map = new HashMap<>();
            map.put("username", user.getUserName());
            map.put("balance", user.getBalance());
            map.put("isActive", user.isActive());
            map.put("transaction history", o);
            return ResponseUtil.success("User details fetched successfully", map);
        } else {
            log.warn("User not found: {}", userName);
            return ResponseUtil.failure(HttpStatus.NOT_FOUND.value(), "User not found with Name: " + userName, null);
        }
    }

    @GetMapping("/active")
    public Boolean checkUserIsActiveOrNot(@RequestParam("userName") String userName) {
        log.info("Checking active status for user: {}", userName);
        return userService.checkActiveOrNot(userName);
    }

    @GetMapping("/activeAccountNumber")
    public String activeAccountNumber(@RequestParam("accountNumber") String accountNumber) {
        log.info("Received request to check account is active or valid accountNumber: {} ", accountNumber);
        String message = userService.checkActiveAccountNumber(accountNumber);

        if (message == null) {
            log.warn("Account number {} is invalid or not found in the database", accountNumber);
            return "Invalid account number";
        }

        log.info("Account number {} check result: {}", accountNumber, message);
        return message;
    }

    @GetMapping("/balanceAvailable")
    public Boolean checkBalance(@RequestParam String accountNumber, @RequestParam Double balance) {
        log.info("Received request to check accountBalance  is sufficient to do transaction with account number: {} ", accountNumber);
        return userService.sufficientBalance(accountNumber, balance);
    }

    @PostMapping("/transferMoney")
    public String transferAmount(@RequestParam String toAccountNumber, @RequestParam String fromAccountNumber, @RequestParam Double balance) {
        log.info("Received request to transfer money from accountNumber: {} to accountNumber {}", fromAccountNumber, toAccountNumber);
        userService.transferBalance(toAccountNumber, fromAccountNumber, balance);
        return "Balance transfer successfully to accountNumber : " + toAccountNumber;
    }

}
