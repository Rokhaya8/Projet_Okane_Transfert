package com.okanetransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenCashSessionRequest(
        @NotNull Long agentId,
        @NotNull Long agencyId,
        @NotNull @DecimalMin(value = "0.00") BigDecimal openingBalance
) {
}
