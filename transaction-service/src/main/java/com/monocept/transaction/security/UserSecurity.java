package com.monocept.transaction.security;

import com.monocept.transaction.entity.UserCredential;
import com.monocept.transaction.repository.UserCredentialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserSecurity implements UserDetailsService {

    private final UserCredentialRepository credentialRepository;

    public UserSecurity(UserCredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("Authenticating user with username: {}", userName);

        UserCredential userCredential = credentialRepository.findByUsername(userName);

        if (userCredential == null) {
            log.warn("Authentication failed: No user found with username '{}'", userName);
            throw new UsernameNotFoundException("User not found with username: " + userName);
        }

        log.info("Authentication successful: User '{}' loaded", userName);

        return User.builder()
                .username(userCredential.getUsername())
                .password(userCredential.getPassword())
                .roles(userCredential.getRole())
                .build();
    }
}
