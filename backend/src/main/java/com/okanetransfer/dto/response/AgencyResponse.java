package com.okanetransfer.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AgencyResponse {

    private Long id;
    private String name;
    private String address;
    private String country;
    private BigDecimal dailyLimit;
    private boolean active;
    private LocalDateTime createdAt;
    private String managerName;
}