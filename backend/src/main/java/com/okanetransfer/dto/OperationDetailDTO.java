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
public class OperationDetailDTO {

    private Long id;
    private String referenceCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal fees;
    private BigDecimal commissionAgency;
    private Long agentId;
    private String agentName;
    private Long senderId;
    private String senderName;
    private String senderPhone;
    private String senderEmail;
    private Long beneficiaryId;
    private String beneficiaryName;
    private String beneficiaryPhone;
    private String beneficiaryCountry;
    private String sentCurrencyCode;
    private String sentCurrencyName;
    private String sentCurrencySymbol;
    private String receivedCurrencyCode;
    private String receivedCurrencyName;
    private String receivedCurrencySymbol;
    private Long sourceAgencyId;
    private String sourceAgencyName;
    private Long destinationAgencyId;
    private String destinationAgencyName;

    public static OperationDetailDTO fromEntity(Transfer transfer) {
        OperationDTO operation = OperationDTO.fromEntity(transfer);
        return OperationDetailDTO.builder()
                .id(operation.getId())
                .referenceCode(operation.getReferenceCode())
                .status(operation.getStatus())
                .createdAt(operation.getCreatedAt())
                .paidAt(operation.getPaidAt())
                .amountSent(operation.getAmountSent())
                .amountReceived(operation.getAmountReceived())
                .fees(operation.getFees())
                .commissionAgency(transfer.getCommissionAgency())
                .agentId(operation.getAgentId())
                .agentName(operation.getAgentName())
                .senderId(operation.getSenderId())
                .senderName(operation.getSenderName())
                .senderPhone(operation.getSenderPhone())
                .senderEmail(transfer.getClient() != null ? transfer.getClient().getEmail() : null)
                .beneficiaryId(operation.getBeneficiaryId())
                .beneficiaryName(operation.getBeneficiaryName())
                .beneficiaryPhone(operation.getBeneficiaryPhone())
                .beneficiaryCountry(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getCountry() : null)
                .sentCurrencyCode(operation.getSentCurrencyCode())
                .sentCurrencyName(operation.getSentCurrencyName())
                .sentCurrencySymbol(operation.getSentCurrencySymbol())
                .receivedCurrencyCode(operation.getReceivedCurrencyCode())
                .receivedCurrencyName(operation.getReceivedCurrencyName())
                .receivedCurrencySymbol(operation.getReceivedCurrencySymbol())
                .sourceAgencyId(transfer.getSourceAgency() != null ? transfer.getSourceAgency().getId() : null)
                .sourceAgencyName(transfer.getSourceAgency() != null ? transfer.getSourceAgency().getName() : null)
                .destinationAgencyId(transfer.getDestinationAgency() != null ? transfer.getDestinationAgency().getId() : null)
                .destinationAgencyName(transfer.getDestinationAgency() != null ? transfer.getDestinationAgency().getName() : null)
                .build();
    }
}
