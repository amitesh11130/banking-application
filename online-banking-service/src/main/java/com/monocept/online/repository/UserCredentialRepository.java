package com.monocept.online.repository;

import com.monocept.online.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    UserCredential findByUsername(String userName);

}
