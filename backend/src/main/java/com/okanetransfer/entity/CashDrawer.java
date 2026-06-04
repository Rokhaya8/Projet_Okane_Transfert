package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cash_drawers")
public class CashDrawer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CashDrawerStatus status;

    @Column(name = "openingtime")
    private LocalDateTime openingTime;

    @Column(name = "closingtime")
    private LocalDateTime closingTime;

    public enum CashDrawerStatus {
        OPEN,
        CLOSED,
        SUSPENDED
    }
}