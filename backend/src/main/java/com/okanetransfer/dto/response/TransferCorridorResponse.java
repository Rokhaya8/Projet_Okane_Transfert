package com.okanetransfer.dto.response;

import lombok.Data;

@Data
public class TransferCorridorResponse {

    private Long id;
    private String sourceCountry;
    private String destinationCountry;

    // Infos de la devise source
    private Long sourceCurrencyId;
    private String sourceCurrencyCode;
    private String sourceCurrencySymbol;

    // Infos de la devise destination
    private Long destinationCurrencyId;
    private String destinationCurrencyCode;
    private String destinationCurrencySymbol;

    private boolean active;
}