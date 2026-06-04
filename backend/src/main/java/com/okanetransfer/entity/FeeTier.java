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

    @Column(name = "minamount", nullable = false)
    private BigDecimal minAmount;

    @Column(name = "maxamount", nullable = false)
    private BigDecimal maxAmount;

    @Column(name = "fixedfee", nullable = false)
    private BigDecimal fixedFee;

    @Column(name = "percentagefee", nullable = false)
    private BigDecimal percentageFee;

    @Column(name = "agencysharepercent", nullable = false)
    private BigDecimal agencySharePercent;
}