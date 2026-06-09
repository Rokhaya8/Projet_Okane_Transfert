package com.okanetransfer.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferSearchDTO {
    private Long id;
    private String codeRetrait;
    private BigDecimal montantEnvoye;
    private String deviseSource;
    private BigDecimal montantRecu;
    private String deviseCible;
    private String nomBeneficiaire;
    private String telephoneBeneficiaire;
    private String statut;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateExpiration;
}
