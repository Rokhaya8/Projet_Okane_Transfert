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

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String country;

    @Column(name = "identitynumber", nullable = false)
    private String identityNumber;

    @Column(name = "watchlistflag", nullable = false)
    private boolean watchlistFlag = false;
}