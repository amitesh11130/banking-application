package com.monocept.transaction.repository;

import com.monocept.transaction.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    UserCredential findByUsername(String userName);

}
