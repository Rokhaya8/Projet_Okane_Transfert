package com.okanetransfer.dto.request;

import lombok.Data;

@Data
public class CurrencyRequest {

    private String code;
    private String name;
    private String symbol;
    private boolean active;
}
