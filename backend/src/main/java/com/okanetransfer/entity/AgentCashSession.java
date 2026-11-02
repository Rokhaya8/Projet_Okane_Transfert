package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "agent_cash_sessions")
public class AgentCashSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal openingBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance;

    @Column(precision = 19, scale = 2)
    private BigDecimal closingBalance;

    @Column(precision = 19, scale = 2)
    private BigDecimal countedAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal discrepancyAmount;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashSessionStatus status;
}
