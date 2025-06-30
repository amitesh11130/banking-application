package com.monocept.online.controller;

import com.monocept.online.dto.ApiResponse;
import com.monocept.online.dto.ResponseUtil;
import com.monocept.online.dto.Status;
import com.monocept.online.dto.UserDto;
import com.monocept.online.entity.RegisterUser;
import com.monocept.online.entity.Role;
import com.monocept.online.service.BankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/onlineBanking")
@Slf4j
public class BankingController {

    private final BankingService bankingService;


    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody UserDto user, Authentication authentication) {
        String loggedInUsername = authentication.getName();

        log.info("Received registration request from '{}', for '{}'", loggedInUsername, user.getUsername());

        if (!loggedInUsername.equals(user.getUsername())) {
            log.warn("Username mismatch: token username = {}, payload username = {}", loggedInUsername, user.getUsername());
            return ResponseUtil.failed(HttpStatus.FORBIDDEN.value(), "You are not authorized to register this user.", null);
        }

        String registerStatus = bankingService.register(user);

        log.info("Registration status for user '{}': {}", user.getUsername(), registerStatus);

        return switch (registerStatus) {
            case "User registration failed." ->
                    ResponseUtil.failed("An unexpected error occurred during user registration.", null);
            case "Service unavailable" ->
                    ResponseUtil.failed(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable", null);
            case "User doesn't exists" ->
                    ResponseUtil.failed(HttpStatus.NOT_FOUND.value(), "User doesn't exists. Registration not allowed.", null);
            case "User exists but is inactive. Registration not allowed." ->
                    ResponseUtil.failed(HttpStatus.CONFLICT.value(), "User exists but is inactive. Registration not allowed.", null);
            case "User already registered." ->
                    ResponseUtil.failed(HttpStatus.CONFLICT.value(), "User already registered.", null);
            default -> ResponseUtil.success("User registration successfully.", null);
        };
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody RegisterUser user, Authentication authentication) {
        String loggedInUsername = authentication.getName();

        log.info("Login request received from '{}', for '{}'", loggedInUsername, user.getUsername());

        if (!loggedInUsername.equals(user.getUsername())) {
            log.warn("Username mismatch: token username = {}, payload username = {}", loggedInUsername, user.getUsername());
            return ResponseUtil.failed(HttpStatus.FORBIDDEN.value(), "You are not authorized  user to login this user.", null);
        }
        boolean loginSuccess = bankingService.login(user);

        if (loginSuccess) {
            log.info("Login successful for user: {}", user.getUsername());

            ApiResponse data = bankingService.fetchUserDetails(user.getUsername());
            log.debug("Fetched user details from user service for '{}': {}", user.getUsername(), data);

            return ResponseUtil.success(HttpStatus.OK.value(), Status.AUTHORIZED, "User logged in successfully.", data.getData());
        } else {
            log.warn("Login failed for user: {}", user.getUsername());
            return ResponseUtil.unAuthorized(HttpStatus.BAD_REQUEST.value(), Status.UNAUTHORIZED, "Login failed. Invalid username or password.");
        }
    }

    @GetMapping("/withdraw")
    public ApiResponse withdraw(@RequestParam String accountNumber, @RequestParam Double balance) {
        log.info("Withdraw request received for accountNumber: {}", accountNumber);
        if (balance == null || balance <= 0) {
            String message = "Withdraw amount must be greater than 0.";
            log.warn("Invalid withdraw attempt: {}", message);
            return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, null);
        }
        ApiResponse response = bankingService.withdrawBalance(accountNumber, balance);
        System.out.println(response);

        if (response == null) {
            ResponseUtil.failed(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable", null);
        }
        return response;
    }

    @PostMapping("/deposit")
    public ApiResponse deposit(@RequestParam String accountNumber, @RequestParam Double balance) {
        log.info("Deposit request received for accountNumber: {}", accountNumber);
        if (balance == null || balance <= 0) {
            String message = "Deposit amount must be greater than 0.";
            log.warn("Invalid deposit attempt: {}", message);
            return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, null);
        }
        ApiResponse response = bankingService.depositBalance(accountNumber, balance);
        if (response == null) {
            ResponseUtil.failed(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable", null);
        }
        return response;
    }

    @PostMapping("/transfer")
    public ApiResponse transferBalance(@RequestParam String toAccountNumber,
                                       @RequestParam Double balance,
                                       @RequestParam String fromAccountNumber) {
        log.info("Transfer balance request received from account: {} to account: {} for amount: {}",
                fromAccountNumber, toAccountNumber, balance);
        if (balance == null || balance <= 0) {
            String message = "Transfer amount must be greater than 0.";
            log.warn("Invalid transfer attempt: {}", message);
            return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, null);
        }
        Map<String, Object> result = bankingService.transferMoney(toAccountNumber, fromAccountNumber, balance);

        if (result == null || result.isEmpty()) {
            log.error("Transfer failed due to insufficient balance or service error");
            return ResponseUtil.failure(HttpStatus.SERVICE_UNAVAILABLE.value(), "Transfer failed", null);
        }

        String message = (String) result.get("message");

        if ("Invalid Account number".equalsIgnoreCase(message)) {
            log.warn("Transfer failed due to invalid account number(s)");
            return ResponseUtil.failure(HttpStatus.BAD_REQUEST.value(), message, null);
        }

        if ("User account number is valid but inActive".equalsIgnoreCase(message)) {
            log.warn("Transfer failed: One or both accounts are inactive");
            return ResponseUtil.failure(HttpStatus.FORBIDDEN.value(), message, null);
        }

        log.info("Transfer completed successfully: {}", message);
        return ResponseUtil.success(message, result);
    }
}
