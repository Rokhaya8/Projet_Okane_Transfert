package com.okanetransfer.dto;

import com.okanetransfer.entity.Agency;
import com.okanetransfer.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerProfileDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Long agencyId;
    private String agencyName;
    private String agencyCountry;

    public static ManagerProfileDTO fromEntity(User manager, Agency agency) {
        return ManagerProfileDTO.builder()
                .id(manager.getId())
                .fullName(manager.getFullName())
                .email(manager.getEmail())
                .phone(manager.getPhone())
                .agencyId(agency != null ? agency.getId() : null)
                .agencyName(agency != null ? agency.getName() : null)
                .agencyCountry(agency != null ? agency.getCountry() : null)
                .build();
    }
}
