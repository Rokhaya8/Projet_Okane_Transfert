package com.okanetransfer.dto;

import lombok.Data;

@Data
public class ReceptionCountryDTO {
    private String country;
    private String currencyCode;
    private String currencyName;
    private String sourceCurrencyCode;
    private String phoneCode;

    public ReceptionCountryDTO(String country, String currencyCode, String currencyName,
                               String sourceCurrencyCode, String phoneCode) {
        this.country = country;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.sourceCurrencyCode = sourceCurrencyCode;
        this.phoneCode = phoneCode;
    }
}