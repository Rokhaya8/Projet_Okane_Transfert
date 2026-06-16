package com.okanetransfer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyPerformanceDTO {

    private Long agencyId;
    private String agencyName;
    private long activeAgents;
    private long openCashDrawers;
    private long pendingValidations;
    private long transactionsThisMonth;
    private BigDecimal averageTransactionAmount;
    private double successRate;
    private BigDecimal monthlyVolume;
    private BigDecimal monthlyFees;
}
