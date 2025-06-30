package com.monocept.user.security;

import com.monocept.user.entity.UserCredential;
import com.monocept.user.repository.UserCredentialRepository;
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
        log.info("Attempting to load user details for username: {}", userName);
        UserCredential userCredential = credentialRepository.findByUsername(userName);
        if (userCredential == null) {
            log.error("User with username '{}' not found", userName);
            throw new UsernameNotFoundException("User not found with username: " + userName);
        }

        log.info("Successfully loaded User details for username: {}", userName);
        return User.builder()
                .username(userCredential.getUsername())
                .password(userCredential.getPassword())
                .roles(userCredential.getRole())
                .build();
    }


}

