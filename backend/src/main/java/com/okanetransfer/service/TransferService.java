package com.okanetransfer.service;

import com.okanetransfer.entity.*;
import com.okanetransfer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private CashDrawerRepository cashDrawerRepository;

    @Autowired
    private FeeService feeService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    // Enregistrer un envoi
    public Transfer registerTransfer(Transfer transfer, Long agentId) {

        // 1. Trouver le corridor
        TransferCorridor corridor = feeService.findCorridor(
                transfer.getCorridor().getSourceCountry(),
                transfer.getBeneficiary().getCountry()
        );

        // 2. Calculer les frais automatiquement
        BigDecimal fees = feeService.calculateFees(
                corridor.getId(),
                transfer.getAmountSent()
        );
        transfer.setFees(fees);

        // 3. Convertir le montant dans la devise destination
        BigDecimal amountReceived = exchangeRateService.convert(
                corridor.getDestinationCurrency().getId(),
                transfer.getAmountSent()
        );
        transfer.setAmountReceived(amountReceived);

        // 4. Générer le code de retrait unique
        transfer.setReferenceCode(generateReferenceCode());

        // 5. Définir statut et expiration
        transfer.setStatus(Transfer.TransferStatus.PENDING);
        transfer.setExpiryDate(LocalDateTime.now().plusDays(7));

        // 6. Sauvegarder
        Transfer savedTransfer = transferRepository.save(transfer);

        // 7. Mettre à jour la caisse
        updateCashDrawer(agentId, transfer.getAmountSent(), true);

        return savedTransfer;
    }

    // Payer un retrait
    public Transfer payTransfer(String referenceCode, Long agentId) {

        // 1. Trouver le transfert
        Transfer transfer = transferRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

        // 2. Vérifier le statut
        if (transfer.getStatus() != Transfer.TransferStatus.PENDING) {
            throw new RuntimeException("Ce transfert ne peut pas être payé");
        }

        // 3. Vérifier l'expiration
        if (transfer.getExpiryDate().isBefore(LocalDateTime.now())) {
            transfer.setStatus(Transfer.TransferStatus.EXPIRED);
            transferRepository.save(transfer);
            throw new RuntimeException("Ce transfert est expiré");
        }

        // 4. Mettre à jour le statut
        transfer.setStatus(Transfer.TransferStatus.PAID);
        transfer.setPaidAt(LocalDateTime.now());

        // 5. Mettre à jour la caisse
        updateCashDrawer(agentId, transfer.getAmountReceived(), false);

        return transferRepository.save(transfer);
    }

    // Chercher par code de retrait
    public Optional<Transfer> findByReferenceCode(String referenceCode) {
        return transferRepository.findByReferenceCode(referenceCode);
    }

    // Transferts d'un agent
    public List<Transfer> getAgentTransfers(Long agentId) {
        return transferRepository.findByAgentId(agentId);
    }

    // Générer code de retrait unique (8 caractères)
    private String generateReferenceCode() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    // Mettre à jour la caisse
    private void updateCashDrawer(Long agentId, BigDecimal amount, boolean isCredit) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

        if (isCredit) {
            cashDrawer.setBalance(cashDrawer.getBalance().add(amount));
        } else {
            cashDrawer.setBalance(cashDrawer.getBalance().subtract(amount));
        }

        cashDrawerRepository.save(cashDrawer);
    }
}