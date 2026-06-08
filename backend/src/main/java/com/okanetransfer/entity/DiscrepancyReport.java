package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "discrepancy_reports")
public class DiscrepancyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_id", nullable = false)
    private Long agentId;

    @ManyToOne
    @JoinColumn(name = "cash_drawer_id", nullable = false)
    private CashDrawer cashDrawer;

    @Column(nullable = false)
    private BigDecimal ecartConstate;

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    private LocalDateTime reportDate = LocalDateTime.now();
}
