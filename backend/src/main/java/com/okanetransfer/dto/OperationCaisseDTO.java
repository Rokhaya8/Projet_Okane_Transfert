package com.okanetransfer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationCaisseDTO {
    
    private Long id;
    private String type;
    private BigDecimal montant;
    private BigDecimal soldeApres;
    private LocalDateTime date;
    private Long referenceTransfertId;
    private String description;
}
