package com.okanetransfer.dto;

import com.okanetransfer.entity.Agent;
import com.okanetransfer.entity.Manager;
import com.okanetransfer.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private boolean active;
    private String role;
    private Long agencyId;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public static UserDTO fromEntity(User user) {
        Long agencyId = null;
        if (user instanceof Agent agent) {
            agencyId = agent.getAgency() != null ? agent.getAgency().getId() : null;
        } else if (user instanceof Manager manager) {
            agencyId = manager.getAgency() != null ? manager.getAgency().getId() : null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.isActive())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .agencyId(agencyId)
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }
}
