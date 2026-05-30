package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "fee_tiers")
public class FeeTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fee_grid_id")
    private FeeGrid feeGrid;

    @Column(nullable = false)
    private BigDecimal minAmount;

    @Column(nullable = false)
    private BigDecimal maxAmount;

    @Column(nullable = false)
    private BigDecimal fixedFee;

    @Column(nullable = false)
    private BigDecimal percentageFee;

    @Column(nullable = false)
    private BigDecimal agencySharePercent;
}