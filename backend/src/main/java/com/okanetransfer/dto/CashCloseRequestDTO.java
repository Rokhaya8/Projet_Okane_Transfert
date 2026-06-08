package com.okanetransfer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashCloseRequestDTO {
    
    @NotNull(message = "Le solde réel saisi est obligatoire")
    @Positive(message = "Le solde réel doit être positif")
    private BigDecimal soldeReelSaisi;
}
