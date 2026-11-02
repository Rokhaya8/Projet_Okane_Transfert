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
    private Long id;                       // identifiant unique de la tranche

    @ManyToOne
    @JoinColumn(name = "fee_grid_id")
    private FeeGrid feeGrid;               // la grille à laquelle appartient cette tranche

    @Column(nullable = false)
    private BigDecimal minAmount;          // montant minimum de la tranche (ex: 0)

    @Column(nullable = false)
    private BigDecimal maxAmount;          // montant maximum de la tranche (ex: 1000)

    @Column(nullable = false)
    private BigDecimal fixedFee;           // frais fixes de la tranche (ex: 50 MAD)

    @Column(nullable = false)
    private BigDecimal percentageFee;      // pourcentage appliqué au montant (ex: 2 pour 2%)

    @Column(nullable = false)
    private BigDecimal agencySharePercent; // part des frais qui revient à l'agence (vs centrale)
}