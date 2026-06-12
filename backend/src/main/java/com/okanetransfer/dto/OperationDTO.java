package com.okanetransfer.dto;

import com.okanetransfer.entity.Currency;
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
public class OperationDTO {

    private Long id;
    private String referenceCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal fees;
    private Long agentId;
    private String agentName;
    private Long senderId;
    private String senderName;
    private String senderPhone;
    private Long beneficiaryId;
    private String beneficiaryName;
    private String beneficiaryPhone;
    private String sentCurrencyCode;
    private String sentCurrencyName;
    private String sentCurrencySymbol;
    private String receivedCurrencyCode;
    private String receivedCurrencyName;
    private String receivedCurrencySymbol;

    public static OperationDTO fromEntity(Transfer transfer) {
        Currency sentCurrency = transfer.getCorridor() != null ? transfer.getCorridor().getSourceCurrency() : null;
        Currency receivedCurrency = transfer.getCorridor() != null ? transfer.getCorridor().getDestinationCurrency() : null;

        return OperationDTO.builder()
                .id(transfer.getId())
                .referenceCode(transfer.getReferenceCode())
                .status(transfer.getStatus() != null ? transfer.getStatus().name() : null)
                .createdAt(transfer.getCreatedAt())
                .paidAt(transfer.getPaidAt())
                .amountSent(transfer.getAmountSent())
                .amountReceived(transfer.getAmountReceived())
                .fees(transfer.getFees())
                .agentId(transfer.getAgent() != null ? transfer.getAgent().getId() : null)
                .agentName(transfer.getAgent() != null ? transfer.getAgent().getFullName() : null)
                .senderId(transfer.getClient() != null ? transfer.getClient().getId() : null)
                .senderName(transfer.getClient() != null ? transfer.getClient().getFullName() : null)
                .senderPhone(transfer.getClient() != null ? transfer.getClient().getPhone() : null)
                .beneficiaryId(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getId() : null)
                .beneficiaryName(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getFullName() : null)
                .beneficiaryPhone(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getPhone() : null)
                .sentCurrencyCode(sentCurrency != null ? sentCurrency.getCode() : null)
                .sentCurrencyName(sentCurrency != null ? sentCurrency.getName() : null)
                .sentCurrencySymbol(sentCurrency != null ? sentCurrency.getSymbol() : null)
                .receivedCurrencyCode(receivedCurrency != null ? receivedCurrency.getCode() : null)
                .receivedCurrencyName(receivedCurrency != null ? receivedCurrency.getName() : null)
                .receivedCurrencySymbol(receivedCurrency != null ? receivedCurrency.getSymbol() : null)
                .build();
    }
}
