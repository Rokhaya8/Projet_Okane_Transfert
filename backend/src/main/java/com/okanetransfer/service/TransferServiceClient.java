package com.okanetransfer.service;

import com.okanetransfer.entity.Transfer;
import com.okanetransfer.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class TransferServiceClient {
    private final TransferRepository transferRepository;

    //injection
    public TransferServiceClient(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Transactional(readOnly = true)
    public Transfer trackTransfer(String refCode) {
        return transferRepository.findByReferenceCodeWithAllRelations(refCode).orElseThrow(() -> new NoSuchElementException("Aucun transfert trouvé avec la référence : " + refCode));
    }

    @Transactional(readOnly = true)
    public List<Transfer> getClientHistory(Long clientId) {
        return transferRepository.findByClientIdWithRelations(clientId);
    }

    @Transactional(readOnly = true)
    public Map<String,Object> getClientDashboardInfo(Long clientId){
        //history complet pour les calculs et l'affichage récent
        List<Transfer> history = transferRepository.findByClientIdWithRelations(clientId);

        // ici la somme globale envoyée
        Double totalSent = transferRepository.sumTotalSentByClient(clientId, Transfer.TransferStatus.PAID);

        long totalTransfersCount = history.size();

        long pendingTransfersCount = history.stream()
                .filter(t -> Transfer.TransferStatus.PENDING.equals(t.getStatus()))
                .count();

        // 5 dernières transactions
        List<Transfer> recentTransfers = history.stream()
                .limit(5)
                .toList();

        // structure de réponse
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMoneySent", totalSent != null ? totalSent : 0.0);
        stats.put("totalTransfersCount", totalTransfersCount);
        stats.put("pendingTransfersCount", pendingTransfersCount);
        stats.put("recentTransfers", recentTransfers);

        return stats;
    }


}
