package com.okanetransfer.dto;

import com.okanetransfer.entity.Transfer;
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
public class TransferDTO {

    private Long id;
    private String referenceCode;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal fees;
    private BigDecimal commissionAgency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private Long agentId;
    private String agentName;
    private Long sourceAgencyId;
    private Long destinationAgencyId;
    private String beneficiaryName;

    public static TransferDTO fromEntity(Transfer transfer) {
        return TransferDTO.builder()
                .id(transfer.getId())
                .referenceCode(transfer.getReferenceCode())
                .amountSent(transfer.getAmountSent())
                .amountReceived(transfer.getAmountReceived())
                .fees(transfer.getFees())
                .commissionAgency(transfer.getCommissionAgency())
                .status(transfer.getStatus() != null ? transfer.getStatus().name() : null)
                .createdAt(transfer.getCreatedAt())
                .paidAt(transfer.getPaidAt())
                .agentId(transfer.getAgent() != null ? transfer.getAgent().getId() : null)
                .agentName(transfer.getAgent() != null ? transfer.getAgent().getFullName() : null)
                .sourceAgencyId(transfer.getSourceAgency() != null ? transfer.getSourceAgency().getId() : null)
                .destinationAgencyId(transfer.getDestinationAgency() != null ? transfer.getDestinationAgency().getId() : null)
                .beneficiaryName(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getFullName() : null)
                .build();
    }
}
