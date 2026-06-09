package com.okanetransfer.service;

import com.okanetransfer.entity.Transfer;
import com.okanetransfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    @Transactional
    public Transfer registerTransfer(Transfer transfer, Long agentId) {
        transfer.setAgentId(agentId);
        transfer.setStatus(Transfer.TransferStatus.EN_ATTENTE);
        return transferRepository.save(transfer);
    }

    @Transactional
    public Transfer payTransfer(String referenceCode, Long agentId) {
        Transfer transfer = transferRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));
        
        transfer.setStatus(Transfer.TransferStatus.PAYE);
        transfer.setPayingAgentId(agentId);
        return transferRepository.save(transfer);
    }

    public Optional<Transfer> findByReferenceCode(String referenceCode) {
        return transferRepository.findByReferenceCode(referenceCode);
    }

    public List<Transfer> getAgentTransfers(Long agentId) {
        return transferRepository.findByAgentId(agentId);
    }
}
