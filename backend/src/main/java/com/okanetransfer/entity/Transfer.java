package com.okanetransfer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(nullable = false, unique = true)
    private String referenceCode;

    @Column(nullable = false)
    private BigDecimal amountSent;

    @Column(nullable = false)
    private BigDecimal amountReceived;

    @Column(nullable = false)
    private BigDecimal fees;

    private BigDecimal commissionAgency;

    private BigDecimal commissionCentral;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Enumerated(EnumType.STRING)
    private ReceptionMode receptionMode;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paidAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paying_agent_id")
    private User payingAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paying_agency_id")
    private Agency payingAgency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_agency_id")
    private Agency sourceAgency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_agency_id")
    private Agency destinationAgency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corridor_id")
    private TransferCorridor corridor;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sender_id")
    private Sender sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"password", "createdAt", "lastLogin"})
    private User client;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;

    public enum TransferStatus {
        EN_ATTENTE,
        PAYE,
        ANNULE,
        EXPIRE,
        PENDING,
        PAID,
        CANCELLED,
        EXPIRED
    }

    public enum ReceptionMode {
        CASH_AGENCE,
        MOBILE_MONEY
    }
}
