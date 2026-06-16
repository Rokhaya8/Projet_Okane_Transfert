package com.okanetransfer.dto.request;

import com.okanetransfer.entity.Transfer.TransferStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private BigDecimal amountSent;
    private BigDecimal amountReceived;
    private BigDecimal fees;
    private BigDecimal commissionAgency;
    private BigDecimal commissionCentral;
    private TransferStatus status;
    private Long agentId;
    private Long agencyId;
    private Long corridorId;
    private Long clientId;        // nullable
    private Long beneficiaryId;
}
