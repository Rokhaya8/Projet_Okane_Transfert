package com.okanetransfer.dto;

import com.okanetransfer.enums.ReportPeriod;
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
public class AgencyReportDTO {

    private Long agencyId;
    private String agencyName;
    private ReportPeriod period;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private long transactionCount;
    private long paidCount;
    private long pendingCount;
    private long cancelledCount;
    private BigDecimal totalVolume;
    private BigDecimal totalFees;
    private BigDecimal totalCommissionAgency;
    private BigDecimal totalRevenue;
}
