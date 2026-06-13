package com.okanetransfer.service;

import com.okanetransfer.dto.response.TransferResponse;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    public List<TransferResponse> getAll() {
        return transferRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransferResponse toResponse(Transfer t) {
        return TransferResponse.builder()
                .id(t.getId())
                .referenceCode(t.getReferenceCode())
                .amountSent(t.getAmountSent())
                .amountReceived(t.getAmountReceived())
                .fees(t.getFees())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .agentName(t.getAgent() != null ? t.getAgent().getFullName() : "-")
                .agencyName(t.getAgency() != null ? t.getAgency().getName() : "-")
                .beneficiaryName(t.getBeneficiary() != null
                        ? t.getBeneficiary().getFullName(): "-")
                .build();
    }
}