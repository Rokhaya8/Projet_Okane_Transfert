package com.okanetransfer.dto;

import lombok.Data;

@Data
public class AgentProfileDTO {

    private Long id;
    private String fullName;
    private String agencyName;
    private String country;

    // Constructeur pour construire le DTO facilement
    public AgentProfileDTO(Long id, String fullName, String agencyName, String country) {
        this.id = id;
        this.fullName = fullName;
        this.agencyName = agencyName;
        this.country = country;
    }
}