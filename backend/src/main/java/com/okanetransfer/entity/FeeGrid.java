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
    private Long id;                    // identifiant unique de la grille

    @ManyToOne
    @JoinColumn(name = "corridor_id")
    private TransferCorridor corridor;  // le corridor auquel cette grille s'applique (ex: Maroc → Sénégal)

    @Column(nullable = false)
    private LocalDateTime validFrom;    // date à partir de laquelle la grille est valable

    @Column(nullable = false)
    private LocalDateTime validTo;      // date jusqu'à laquelle la grille est valable

    @Column(nullable = false)
    private boolean active = true;      // grille active (true) ou désactivée (false)
}