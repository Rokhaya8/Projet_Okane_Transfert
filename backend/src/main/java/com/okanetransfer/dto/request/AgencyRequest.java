package com.okanetransfer.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AgencyRequest {

    private String name;
    private String address;
    private String country;
    private BigDecimal dailyLimit;
    private boolean active = true;
    private Long managerId;
}
