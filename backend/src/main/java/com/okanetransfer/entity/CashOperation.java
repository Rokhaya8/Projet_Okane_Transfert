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

    @ManyToOne
    @JoinColumn(name = "cash_drawer_id", nullable = false)
    private CashDrawer cashDrawer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private LocalDateTime operationDate = LocalDateTime.now();

    // Référence au transfert lié (nullable pour clôture/écart)
    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;

    private String description;

    public enum OperationType {
        ENCAISSEMENT,   // Entrée (envoi de fonds)
        DECAISSEMENT,   // Sortie (paiement retrait)
        CLOTURE         // Clôture journalière
    }
}
