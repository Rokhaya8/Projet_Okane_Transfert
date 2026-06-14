package com.okanetransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayTransferRequest(
        @NotNull Long agentId,
        @NotBlank String referenceCode,
        @NotBlank String beneficiaryIdentityNumber
) {
}
