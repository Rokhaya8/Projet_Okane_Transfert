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
public class AgentPerformanceDTO {

    private Long agentId;
    private String agentName;
    private long totalOperations;
    private long paidOperations;
    private long pendingOperations;
    private BigDecimal totalAmount;
}
