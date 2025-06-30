package com.monocept.user.repository;

import com.monocept.user.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    UserCredential findByUsername(String userName);

}
