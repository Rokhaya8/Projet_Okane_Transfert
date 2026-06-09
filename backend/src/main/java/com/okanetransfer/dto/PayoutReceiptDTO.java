package com.okanetransfer.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PayoutReceiptDTO {
    private String codeRetrait;
    private BigDecimal montantPaye;
    private String devise;
    private String nomBeneficiaire;
    private String telephoneBeneficiaire;
    private LocalDateTime datePaiement;
    private BigDecimal frais;
    private String nomAgentPaiement;
    private String agenceNom;
}
