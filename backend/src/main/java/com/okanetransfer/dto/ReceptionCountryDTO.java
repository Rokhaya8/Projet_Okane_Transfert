package com.okanetransfer.dto;

import lombok.Data;

@Data
public class ReceptionCountryDTO {

    private String country;             // ex: "Sénégal"
    private String currencyCode;        // devise de réception, ex: "XOF"
    private String currencyName;        // ex: "Franc CFA"
    private String sourceCurrencyCode;  // devise d'envoi, ex: "MAD"

    public ReceptionCountryDTO(String country, String currencyCode, String currencyName, String sourceCurrencyCode) {
        this.country = country;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.sourceCurrencyCode = sourceCurrencyCode;
    }
}