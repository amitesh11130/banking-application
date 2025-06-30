package com.monocept.online.service;

import com.monocept.online.dto.ApiResponse;
import com.monocept.online.dto.UserDto;
import com.monocept.online.entity.RegisterUser;
import com.monocept.online.repository.RegisterUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankingService {

    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;
    private final RegisterUserRepository registerUserRepository;


    public String register(UserDto userDto) {
        log.debug("Checking user existence for: {}", userDto.getUsername());
        ApiResponse userDetails = userServiceClient.getUserDetails(userDto.getUsername());
        if (userDetails == null || userDetails.getData() == null) {
            log.warn("User '{}' not found in user service", userDto.getUsername());
            return "User doesn't exists";
        }

        log.debug("User '{}' exists. Checking active status...", userDto.getUsername());
        Boolean isActive = userServiceClient.checkUserIsActiveOrNot(userDto.getUsername());

        if (isActive == null) {
            log.error("Failed to fetch active status for user: {}", userDto.getUsername());
            return "Service unavailable";
        }

        if (Boolean.TRUE.equals(isActive)) {
            RegisterUser existingUser = registerUserRepository.findByUsername(userDto.getUsername());
            if (existingUser != null) {
                log.info("User '{}' is already registered", userDto.getUsername());
                return "User already registered.";
            }

            log.info("Registering new user: {}", userDto.getUsername());
            RegisterUser saveUser = RegisterUser.builder()
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .build();

            RegisterUser savedUser = registerUserRepository.save(saveUser);
            if (ObjectUtils.isEmpty(savedUser) || savedUser.getId() == null) {
                log.error("User '{}' registration failed during DB save", userDto.getUsername());
                return "User registration failed.";
            }

            log.info("User '{}' registered successfully", userDto.getUsername());
            return "User registered successfully.";
        } else {
            log.warn("User '{}' exists but is inactive", userDto.getUsername());
            return "User exists but is inactive. Registration not allowed.";
        }
    }

    public boolean login(RegisterUser user) {
        log.debug("Attempting login for user: {}", user.getUsername());

        RegisterUser getUser = registerUserRepository.findByUsername(user.getUsername());
        if (getUser == null) {
            log.warn("Login failed: No user found with username '{}'", user.getUsername());
            return false;
        }

        boolean passwordMatches = passwordEncoder.matches(user.getPassword(), getUser.getPassword());
        if (!passwordMatches) {
            log.warn("Login failed: Invalid password for user '{}'", user.getUsername());
        }

        return passwordMatches;
    }

    public ApiResponse fetchUserDetails(String userName) {
        log.debug("Fetching user details from user service for: {}", userName);
        ApiResponse response = userServiceClient.getUserDetails(userName);
        log.debug("Received response from user service for '{}': {}", userName, response);
        return response;
    }

    public ApiResponse withdrawBalance(String accountNumber, Double balance) {
        return userServiceClient.withdrawBalance(accountNumber, balance);
    }

    public ApiResponse depositBalance(String accountNumber, Double balance) {
        return userServiceClient.depositBalance(accountNumber, balance);
    }

    public Map<String, Object> transferMoney(String toAccountNumber, String fromAccountNumber, Double balance) {

        Map<String, Object> map = new HashMap<>();
        String checkStatus1 = userServiceClient.activeAccountNumber(toAccountNumber);
        String checkStatus2 = userServiceClient.activeAccountNumber(fromAccountNumber);

        if ("Invalid account number".equals(checkStatus1) || "Invalid account number".equals(checkStatus2)) {
            map.put("message", "Invalid Account number");
            return map;
        }
        if ("User account number is valid but inActive".equals(checkStatus1) ||
                "User account number is valid but inActive".equals(checkStatus2)) {
            map.put("message", "User account number is valid but inActive");
            return map;
        } else {
            if (userServiceClient.checkBalance(fromAccountNumber, balance)) {
                String message = userServiceClient.transferAmount(toAccountNumber, fromAccountNumber, balance);
                if (message.isEmpty()) {
                    map.put("message", "Service unavailable");
                    return map;
                }
                map.put("message", message);
                return map;
            }
        }
        return null;
    }
}
