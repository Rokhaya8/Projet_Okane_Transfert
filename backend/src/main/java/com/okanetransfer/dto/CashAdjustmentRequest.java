package com.okanetransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CashAdjustmentRequest(
        @NotNull BigDecimal amount,
        @NotBlank String reference
) {
}
