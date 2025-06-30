package com.monocept.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "entity_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;
    private String password;

    @Column(unique = true)
    private String accountNumber;
    private String accountType;

    @Column(unique = true)
    private String aadharNumber;

    @Column(unique = true)
    private String contactNumber;
    private String address;
    private Double balance;
    private boolean isActive;

}
