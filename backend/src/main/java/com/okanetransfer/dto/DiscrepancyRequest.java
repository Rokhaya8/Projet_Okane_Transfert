package com.okanetransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DiscrepancyRequest {

    @NotNull
    private BigDecimal countedAmount;

    @NotBlank
    private String reason;

    public BigDecimal getCountedAmount() { return countedAmount; }
    public void setCountedAmount(BigDecimal v) { this.countedAmount = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
}
