package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "referencecode", nullable = false, unique = true)
    private String referenceCode;

    @Column(name = "amountsent", nullable = false)
    private BigDecimal amountSent;

    @Column(name = "amountreceived", nullable = false)
    private BigDecimal amountReceived;

    @Column(nullable = false)
    private BigDecimal fees;

    @Column(name = "commissionagency", nullable = false)
    private BigDecimal commissionAgency;

    @Column(name = "commissioncentral", nullable = false)
    private BigDecimal commissionCentral;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "createdat", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paidat")
    private LocalDateTime paidAt;

    @Column(name = "expirydate")
    private LocalDateTime expiryDate;

    // Relations
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne
    @JoinColumn(name = "corridor_id")
    private TransferCorridor corridor;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client; // nullable

    @ManyToOne
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;

    public enum TransferStatus {
        PENDING,
        PAID,
        CANCELLED,
        EXPIRED
    }
}