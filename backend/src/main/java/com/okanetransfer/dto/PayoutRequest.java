package com.okanetransfer.dto;

import jakarta.validation.constraints.NotBlank;

public class PayoutRequest {

    @NotBlank
    private String identityNumber;

    private String identityType;

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String v) { this.identityNumber = v; }
    public String getIdentityType() { return identityType; }
    public void setIdentityType(String v) { this.identityType = v; }
}
