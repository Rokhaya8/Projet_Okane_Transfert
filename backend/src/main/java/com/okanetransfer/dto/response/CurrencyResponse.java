package com.okanetransfer.dto.response;

import lombok.Data;

@Data
public class CurrencyResponse {

    private Long id;
    private String code;
    private String name;
    private String symbol;
    private boolean active;
}