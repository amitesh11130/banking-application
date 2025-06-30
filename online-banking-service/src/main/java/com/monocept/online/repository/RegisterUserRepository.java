package com.monocept.online.repository;

import com.monocept.online.entity.RegisterUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterUserRepository extends JpaRepository<RegisterUser, Long> {
    RegisterUser findByUsername(String username);

}
