package com.okanetransfer.dto;

import com.okanetransfer.entity.SensitiveOperation;
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
public class SensitiveOperationDTO {

    private Long id;
    private String operationType;
    private String status;
    private Long transferId;
    private String transferReference;
    private Long requestedById;
    private String requestedByName;
    private BigDecimal amount;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public static SensitiveOperationDTO fromEntity(SensitiveOperation op) {
        return SensitiveOperationDTO.builder()
                .id(op.getId())
                .operationType(op.getOperationType() != null ? op.getOperationType().name() : null)
                .status(op.getStatus() != null ? op.getStatus().name() : null)
                .transferId(op.getTransfer() != null ? op.getTransfer().getId() : null)
                .transferReference(op.getTransfer() != null ? op.getTransfer().getReferenceCode() : null)
                .requestedById(op.getRequestedBy() != null ? op.getRequestedBy().getId() : null)
                .requestedByName(op.getRequestedBy() != null ? op.getRequestedBy().getFullName() : null)
                .amount(op.getAmount())
                .rejectionReason(op.getRejectionReason())
                .createdAt(op.getCreatedAt())
                .processedAt(op.getProcessedAt())
                .build();
    }
}
