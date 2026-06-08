package com.okanetransfer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscrepancyRequestDTO {
    
    @NotNull(message = "L'écart constaté est obligatoire")
    private BigDecimal ecartConstate;
    
    private String commentaire;
}
