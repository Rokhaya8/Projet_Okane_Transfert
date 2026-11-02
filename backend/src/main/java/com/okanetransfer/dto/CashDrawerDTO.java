package com.okanetransfer.dto;

import com.okanetransfer.entity.CashDrawer;
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
public class CashDrawerDTO {

    private Long id;
    private Long agentId;
    private String agentName;
    private Long agencyId;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private BigDecimal closingBalance;
    private String status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    public static CashDrawerDTO fromEntity(CashDrawer drawer) {
        return CashDrawerDTO.builder()
                .id(drawer.getId())
                .agentId(drawer.getAgent() != null ? drawer.getAgent().getId() : null)
                .agentName(drawer.getAgent() != null ? drawer.getAgent().getFullName() : null)
                .agencyId(drawer.getAgency() != null ? drawer.getAgency().getId() : null)
                .openingBalance(drawer.getOpeningBalance())
                .currentBalance(drawer.getCurrentBalance())
                .closingBalance(drawer.getClosingBalance())
                .status(drawer.getStatus() != null ? drawer.getStatus().name() : null)
                .openedAt(drawer.getOpenedAt())
                .closedAt(drawer.getClosedAt())
                .build();
    }
}
