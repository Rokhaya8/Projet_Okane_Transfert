package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fee_grids")
public class FeeGrid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "corridor_id")
    private TransferCorridor corridor;

    @Column(name = "validfrom", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "validto", nullable = false)
    private LocalDateTime validTo;

    @Column(nullable = false)
    private boolean active = true;
}