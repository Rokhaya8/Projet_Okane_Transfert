package com.okanetransfer.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SimulationDTO {
    private BigDecimal amountSent;
    private BigDecimal fees;
    private BigDecimal totalToPay;
    private BigDecimal amountReceived;
    private String sourceCurrency;
    private String destinationCurrency;
}