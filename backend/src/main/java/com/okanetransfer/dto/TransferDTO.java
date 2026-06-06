package com.okanetransfer.dto;

import com.okanetransfer.entity.Transfer;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferDTO {

    private Long id;
    private String referenceCode;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal fees;
    private String status;
    private String receptionMode;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private String senderName;
    private String beneficiaryName;
    private String agentName;

    // Méthode qui transforme une entité Transfer en DTO
    public static TransferDTO fromEntity(Transfer transfer) {
        TransferDTO dto = new TransferDTO();
        dto.setId(transfer.getId());
        dto.setReferenceCode(transfer.getReferenceCode());
        dto.setAmountSent(transfer.getAmountSent());
        dto.setAmountReceived(transfer.getAmountReceived());
        dto.setFees(transfer.getFees());
        dto.setStatus(transfer.getStatus() != null ? transfer.getStatus().name() : null);
        dto.setReceptionMode(transfer.getReceptionMode() != null ? transfer.getReceptionMode().name() : null);
        dto.setCreatedAt(transfer.getCreatedAt());
        dto.setExpiryDate(transfer.getExpiryDate());
        dto.setSenderName(transfer.getSender() != null ? transfer.getSender().getFullName() : null);
        dto.setBeneficiaryName(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getFullName() : null);
        dto.setAgentName(transfer.getAgent() != null ? transfer.getAgent().getFullName() : null);
        return dto;
    }
}