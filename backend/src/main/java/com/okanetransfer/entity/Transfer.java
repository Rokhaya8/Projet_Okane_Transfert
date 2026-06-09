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

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

    private LocalDateTime expiryDate;

    // Relations
    @ManyToOne
    @JoinColumn(name = "agent_id", insertable = false, updatable = false)
    private User agent;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne
    @JoinColumn(name = "corridor_id")
    private TransferCorridor corridor;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;

    @ManyToOne
    @JoinColumn(name = "paying_agent_id", insertable = false, updatable = false)
    private User payingAgent;

    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "paying_agent_id")
    private Long payingAgentId;

    @Column(name = "beneficiary_id_doc")
    private String beneficiaryIdDocument;

    @Column(name = "sent_amount", nullable = false)
    private BigDecimal sentAmount;

    @Column(name = "received_amount", nullable = false)
    private BigDecimal receivedAmount;

    @Column(name = "source_currency")
    private String sourceCurrency;

    @Column(name = "destination_currency")
    private String destinationCurrency;

    @Column(name = "source_country")
    private String sourceCountry;

    @Column(name = "destination_country")
    private String destinationCountry;

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "beneficiary_phone")
    private String beneficiaryPhone;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    public enum TransferStatus {
        EN_ATTENTE,
        PAYE,
        ANNULE,
        EXPIRE
    }
}