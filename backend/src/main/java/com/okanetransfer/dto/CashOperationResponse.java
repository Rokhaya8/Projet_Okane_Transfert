package com.okanetransfer.dto;

import com.okanetransfer.entity.CashOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashOperationResponse(
        Long id,
        Long cashSessionId,
        CashOperationType operationType,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        LocalDateTime operationDate,
        String reference,
        Long transferId
) {
}
