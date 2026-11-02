package com.okanetransfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentProfileDTO {
    private Long id;
    private String fullName;
    private String agencyName;
    private String country;
}