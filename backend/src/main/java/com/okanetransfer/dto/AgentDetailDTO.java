package com.okanetransfer.dto;

import com.okanetransfer.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDetailDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private long totalTransfers;
    private long paidTransfers;
    private long pendingTransfers;
    private BigDecimal totalAmountProcessed;

    public static AgentDetailDTO fromEntity(
            User agent,
            long totalTransfers,
            long paidTransfers,
            long pendingTransfers,
            BigDecimal totalAmountProcessed) {
        return AgentDetailDTO.builder()
                .id(agent.getId())
                .fullName(agent.getFullName())
                .email(agent.getEmail())
                .phone(agent.getPhone())
                .active(agent.isActive())
                .createdAt(agent.getCreatedAt())
                .lastLogin(agent.getLastLogin())
                .totalTransfers(totalTransfers)
                .paidTransfers(paidTransfers)
                .pendingTransfers(pendingTransfers)
                .totalAmountProcessed(totalAmountProcessed)
                .build();
    }
}
