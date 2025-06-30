package com.monocept.user.service;

import com.monocept.user.entity.User;
import com.monocept.user.entity.UserCredential;
import com.monocept.user.repository.UserCredentialRepository;
import com.monocept.user.repository.UserRepository;
import com.monocept.user.request.TransactionDto;
import com.monocept.user.request.UserDto;
import com.monocept.user.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialRepository userCredentialRepository;
    private final TransactionServiceCall transactionServiceCall;

    public Map<String, Object> createUser(UserDto userDto) {
        log.info("Attempting to create user: {}", userDto.getUserName());
        Map<String, Object> map = new HashMap<>();
        try {
            if (userDto.getUserName() == null || userDto.getPassword() == null) {
                log.error("User data is incomplete");
                return null;
            }

            if (userRepository.existsByUserNameOrAadharNumber(userDto.getUserName(), userDto.getAadharNumber())) {
                String message = "User already present in database !";
                log.warn(message);
                map.put("message", message);
                return map;
            }
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            User user = convertDtoToEntity(userDto);
            User save = userRepository.save(user);
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(save.getAccountNumber())
                    .type("DEPOSIT").amount(user.getBalance()).build();
            Object transaction = createTransaction(transactionDto);
            save.setPassword(convertDummyPassword());
            map.put("user", save);
            map.put("transaction", transaction);
            log.info("User created and initial deposit recorded for user: {}", user.getUserName());
            return map;
        } catch (Exception e) {
            log.error("Error creating user {}: {}", userDto.getUserName(), e.getMessage(), e);
            return null;
        }
    }

    public Object createTransaction(TransactionDto transactionDto) {
        ApiResponse apiResponse = transactionServiceCall.createTransaction(transactionDto);
        return apiResponse.getData();

    }

    private User convertDtoToEntity(UserDto userDto) {
        return User.builder()
                .userName(userDto.getUserName())
                .password(userDto.getPassword())
                .aadharNumber(userDto.getAadharNumber())
                .contactNumber(userDto.getContactNumber())
                .accountNumber(generateAccountNumber())
                .accountType(userDto.getAccountType())
                .address(userDto.getAddress())
                .balance(userDto.getBalance())
                .isActive(userDto.isActive())
                .build();

    }

    public static String generateAccountNumber() {
        String prefix = "484810";
        Random random = new Random();

        StringBuilder accountNumber = new StringBuilder(prefix);
        for (int i = 0; i < 6; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }

    private String convertDummyPassword() {
        return "***************";
    }

    public Map<String, Object> depositBalance(String accountNumber, Double balance) {
        log.info("Processing deposit for user ID: {} with amount: {}", accountNumber, balance);
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByAccountNumber(accountNumber);

        if (user != null) {
            if (user.isActive()) {
                user.setBalance(user.getBalance() + balance);
                TransactionDto transactionDto = TransactionDto.builder()
                        .accountNumber(user.getAccountNumber())
                        .amount(balance)
                        .type("DEPOSIT").build();
                Object transaction = createTransaction(transactionDto);
                userRepository.save(user);
                response.put("message", "Deposit successful");
                response.put("available balance", user.getBalance());
                response.put("transaction", transaction);
                log.info("Deposit completed for user accountNumber: {}", accountNumber);
            } else {
                response.put("message", "The user account is inactive, so the deposit cannot be processed at this time.");
                log.warn("Deposit attempt on inactive account: {}", accountNumber);
            }
        } else {
            response.put("message", "User not found with accountNumber: " + accountNumber);
            log.warn("Deposit failed: user accountNumber {} not found", accountNumber);
        }
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    public Map<String, Object> withdrawBalance(String accountNumber, Double balance) {
        log.info("Processing withdrawal for user accountNumber: {} with amount: {}", accountNumber, balance);
        Map<String, Object> response = new HashMap<>();
        User findUser = userRepository.findByAccountNumber(accountNumber);

        if (findUser == null) {
            String msg = "User not found with accountNumber: " + accountNumber;
            response.put("message", msg);
            log.warn(msg);
        } else {
            if (!findUser.isActive()) {
                response.put("message", "The user account is inactive, so the withdraw cannot be processed at this time.");
                log.warn("Withdrawal attempt on inactive accountNumber: {}", accountNumber);
            } else if (findUser.getBalance() >= balance) {
                findUser.setBalance(findUser.getBalance() - balance);
                TransactionDto transactionDto = TransactionDto.builder()
                        .accountNumber(findUser.getAccountNumber())
                        .amount(balance)
                        .type("WITHDRAW").build();
                Object transaction = createTransaction(transactionDto);
                userRepository.save(findUser);
                response.put("message", "Withdrawal successful");
                response.put("available balance", findUser.getBalance());
                response.put("transaction", transaction);
                log.info("Withdrawal completed for user accountNumber: {}", accountNumber);
            } else {
                response.put("message", "Insufficient balance to withdraw");
                response.put("available balance", findUser.getBalance());
                log.warn("Withdrawal failed for user accountNumber {}: insufficient balance", accountNumber);
            }
        }
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    public User getUserByUserName(String name) {
        User byUserName = userRepository.findByUserName(name);
        byUserName.setPassword(convertDummyPassword());
        return byUserName;
    }

    public Object transactionHistory(String accountNumber) {
        log.info("Fetching transaction history for user AccountNumber: {}", accountNumber);
        ApiResponse apiResponse = transactionServiceCall.fetchAllTransaction(accountNumber);

        log.info("Found transaction(s) for user accountNumber: {}", accountNumber);
        return apiResponse.getData();
    }

    public Boolean checkActiveOrNot(String userName) {
        log.info("Checking if user '{}' is active", userName);
        User byName = userRepository.findByUserName(userName);
        log.info("User '{}' active status: {}", userName, byName.isActive());
        return byName.isActive();
    }

    public List<User> getAllUser() {
        log.debug("Fetching all users from repository");
        List<User> users = userRepository.findAll();
        log.debug("Fetched {} users from repository", users.size());
        return users.stream()
                .map(user -> {
                    User dto = new User();
                    dto.setId(user.getId());
                    dto.setUserName(user.getUserName());
                    dto.setAadharNumber(user.getAadharNumber());
                    dto.setContactNumber(user.getContactNumber());
                    dto.setAccountNumber(user.getAccountNumber());
                    dto.setAccountType(user.getAccountType());
                    dto.setAddress(user.getAddress());
                    dto.setBalance(user.getBalance());
                    dto.setActive(user.isActive());
                    dto.setPassword(convertDummyPassword());
                    return dto;
                }).toList();
    }


    public String checkActiveAccountNumber(String toAccountNumber) {
        User byAccountNumber = userRepository.findByAccountNumber(toAccountNumber);
        if (byAccountNumber != null) {
            if (byAccountNumber.isActive()) {
                return "User account number is valid and active";
            }
            return "User account number is valid but inActive";
        }
        return null;
    }

    public Boolean sufficientBalance(String fromAccountNumber, Double balance) {
        User byAccountNumber = userRepository.findByAccountNumber(fromAccountNumber);
        if (byAccountNumber != null) {
            return byAccountNumber.getBalance() >= balance;
        }
        return null;
    }

    @Transactional
    public void transferBalance(String toAccountNumber, String fromAccountNumber, Double balance) {

        withdrawBalance(fromAccountNumber, balance);
        depositBalance(toAccountNumber, balance);
    }

    public UserCredential createUserCredential(UserCredential userCredential) {
        return userCredentialRepository.save(userCredential);
    }
}
