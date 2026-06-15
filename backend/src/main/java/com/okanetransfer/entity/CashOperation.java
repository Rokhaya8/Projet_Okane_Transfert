package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cash_operations")
public class CashOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cash_session_id", nullable = false)
    private AgentCashSession cashSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashOperationType operationType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private LocalDateTime operationDate;

    @Column(nullable = false)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;
}
