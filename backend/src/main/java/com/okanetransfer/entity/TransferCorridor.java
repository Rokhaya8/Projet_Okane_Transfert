package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transfer_corridors")
public class TransferCorridor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceCountry;

    @Column(nullable = false)
    private String destinationCountry;

    @ManyToOne
    @JoinColumn(name = "source_currency_id")
    private Currency sourceCurrency;

    @ManyToOne
    @JoinColumn(name = "destination_currency_id")
    private Currency destinationCurrency;

    @Column(nullable = false)
    private boolean active = true;
}