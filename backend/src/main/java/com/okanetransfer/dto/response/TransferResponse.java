package com.okanetransfer.dto.response;

import com.okanetransfer.entity.Transfer.TransferStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferResponse {
    private Long id;
    private String referenceCode;
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal fees;
    private TransferStatus status;
    private LocalDateTime createdAt;
    private String agentName;       // fullName de l'agent
    private String agencyName;
    private String beneficiaryName;
}