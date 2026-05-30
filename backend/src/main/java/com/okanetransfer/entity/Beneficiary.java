package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "beneficiaries")
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String identityNumber;

    @Column(nullable = false)
    private boolean watchlistFlag = false;
}