package com.okanetransfer.dto.request;

import lombok.Data;

@Data
public class TransferCorridorRequest {

    private String sourceCountry;
    private String destinationCountry;
    private Long sourceCurrencyId;
    private Long destinationCurrencyId;
    private boolean active;
}