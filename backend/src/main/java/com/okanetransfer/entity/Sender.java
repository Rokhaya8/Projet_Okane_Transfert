package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "senders")
public class Sender {

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
    private String identityType;    // CIN, Passeport... (champ présent dans ton formulaire expéditeur)

    @Column(nullable = false)
    private String identityNumber;
}