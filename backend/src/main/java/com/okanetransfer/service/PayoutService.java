package com.okanetransfer.service;

import com.okanetransfer.dto.*;
import com.okanetransfer.entity.*;
import com.okanetransfer.exception.*;
import com.okanetransfer.repository.*;
import com.okanetransfer.util.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private final TransferRepository transferRepository;
    private final CashDrawerRepository cashDrawerRepository;
    private final CashOperationRepository cashOperationRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final EncryptionService encryptionService;

    private static final int EXPIRATION_DAYS = 30;

    /**
     * Recherche un transfert par code ou téléphone bénéficiaire
     * Si aucun critère n'est fourni, retourne tous les transferts EN_ATTENTE des dernières 24h
     */
    public List<TransferSearchDTO> searchTransfer(String code, String telephoneBeneficiaire) {
        List<Transfer> transfers;

        if (code != null && !code.isEmpty()) {
            transfers = transferRepository.findByReferenceCodeAndStatus(code, Transfer.TransferStatus.EN_ATTENTE)
                    .map(List::of)
                    .orElse(List.of());
        } else if (telephoneBeneficiaire != null && !telephoneBeneficiaire.isEmpty()) {
            transfers = transferRepository.findByBeneficiaryPhoneAndStatus(telephoneBeneficiaire, Transfer.TransferStatus.EN_ATTENTE);
        } else {
            // Par défaut, retourner tous les transferts EN_ATTENTE des dernières 24h
            LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
            transfers = transferRepository.findByStatusAndCreatedAtAfter(Transfer.TransferStatus.EN_ATTENTE, last24Hours);
        }

        return transfers.stream()
                .map(this::mapToSearchDTO)
                .collect(Collectors.toList());
    }

    /**
     * Payer un retrait
     */
    @Transactional
    public PayoutReceiptDTO payoutTransfer(Long transferId, Long agentId, PayoutRequestDTO request, String ipAddress) {
        // Récupérer le transfert
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new TransferNotFoundException("Transfert introuvable"));

        // Vérifier le statut
        if (transfer.getStatus() != Transfer.TransferStatus.EN_ATTENTE) {
            throw new InvalidTransferStatusException("Le transfert n'est pas en attente de paiement. Statut actuel: " + transfer.getStatus());
        }

        // Vérifier l'expiration
        LocalDateTime expirationDate = transfer.getCreatedAt().plusDays(EXPIRATION_DAYS);
        if (LocalDateTime.now().isAfter(expirationDate)) {
            transfer.setStatus(Transfer.TransferStatus.EXPIRE);
            transferRepository.save(transfer);
            throw new TransferExpiredException("Le transfert a expiré. Date limite: " + expirationDate);
        }

        // Récupérer l'agent et sa caisse
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new IllegalStateException("Agent introuvable"));

        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new IllegalStateException("Caisse introuvable pour cet agent"));

        // Vérifier le solde de la caisse
        BigDecimal montantAPayer = transfer.getReceivedAmount();
        if (cashDrawer.getBalance().compareTo(montantAPayer) < 0) {
            throw new InsufficientCashBalanceException(
                    String.format("Solde insuffisant. Requis: %s, Disponible: %s", montantAPayer, cashDrawer.getBalance())
            );
        }

        // Chiffrer la pièce d'identité
        String encryptedId = encryptionService.encrypt(request.getPieceIdentiteBeneficiaire());
        transfer.setBeneficiaryIdDocument(encryptedId);

        // Mettre à jour le transfert
        transfer.setStatus(Transfer.TransferStatus.PAYE);
        transfer.setPaymentDate(LocalDateTime.now());
        transfer.setPayingAgentId(agentId);
        transferRepository.save(transfer);

        // Débiter la caisse
        cashDrawer.setBalance(cashDrawer.getBalance().subtract(montantAPayer));
        cashDrawerRepository.save(cashDrawer);

        // Créer une opération de caisse
        CashOperation operation = new CashOperation();
        operation.setCashDrawer(cashDrawer);
        operation.setType(CashOperation.OperationType.DECAISSEMENT);
        operation.setAmount(montantAPayer);
        operation.setBalanceAfter(cashDrawer.getBalance());
        operation.setTransfer(transfer);
        operation.setDescription("Paiement retrait - Code: " + transfer.getReferenceCode());
        cashOperationRepository.save(operation);

        // Journaliser l'action
        auditService.logAction(
                agentId,
                "PAYOUT_TRANSFER",
                "Transfer",
                transfer.getId(),
                String.format("Paiement effectué - Code: %s, Montant: %s", transfer.getReferenceCode(), montantAPayer),
                ipAddress
        );

        // Retourner le reçu
        return PayoutReceiptDTO.builder()
                .codeRetrait(transfer.getReferenceCode())
                .montantPaye(montantAPayer)
                .devise(transfer.getDestinationCurrency())
                .nomBeneficiaire(transfer.getBeneficiaryName())
                .telephoneBeneficiaire(transfer.getBeneficiaryPhone())
                .datePaiement(transfer.getPaymentDate())
                .frais(transfer.getFees())
                .nomAgentPaiement(agent.getFullName())
                .agenceNom(cashDrawer.getAgency() != null ? cashDrawer.getAgency().getName() : "N/A")
                .build();
    }

    /**
     * Historique des transferts de l'agent
     */
    public Page<TransferHistoryDTO> getTransferHistory(Long agentId,
                                                        Transfer.TransferStatus status,
                                                        LocalDateTime startDate,
                                                        LocalDateTime endDate,
                                                        String corridor,
                                                        String search,
                                                        Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        
        // Si startDate est null, prendre les dernières 24h par défaut
        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.now().minusHours(24);
        
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        
        List<Transfer> transfers = transferRepository.findHistoryByAgent(
                agentId, statusStr, effectiveStartDate, endDate, search, corridor, limit, offset
        );
        
        long total = transferRepository.countHistoryByAgent(
                agentId, statusStr, effectiveStartDate, endDate, search, corridor
        );
        
        List<TransferHistoryDTO> content = transfers.stream()
                .map(this::mapToHistoryDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(content, pageable, total);
    }

    private TransferSearchDTO mapToSearchDTO(Transfer transfer) {
        LocalDateTime expirationDate = transfer.getCreatedAt().plusDays(EXPIRATION_DAYS);

        return TransferSearchDTO.builder()
                .id(transfer.getId())
                .codeRetrait(transfer.getReferenceCode())
                .montantEnvoye(transfer.getSentAmount())
                .deviseSource(transfer.getSourceCurrency())
                .montantRecu(transfer.getReceivedAmount())
                .deviseCible(transfer.getDestinationCurrency())
                .nomBeneficiaire(transfer.getBeneficiaryName())
                .telephoneBeneficiaire(transfer.getBeneficiaryPhone())
                .statut(transfer.getStatus().name())
                .dateEnvoi(transfer.getCreatedAt())
                .dateExpiration(expirationDate)
                .build();
    }

    private TransferHistoryDTO mapToHistoryDTO(Transfer transfer) {
        String nomAgentSaisie = "N/A";
        String nomAgentPaiement = "N/A";

        if (transfer.getAgentId() != null) {
            nomAgentSaisie = userRepository.findById(transfer.getAgentId())
                    .map(User::getFullName)
                    .orElse("N/A");
        }

        if (transfer.getPayingAgentId() != null) {
            nomAgentPaiement = userRepository.findById(transfer.getPayingAgentId())
                    .map(User::getFullName)
                    .orElse("N/A");
        }

        return TransferHistoryDTO.builder()
                .id(transfer.getId())
                .codeRetrait(transfer.getReferenceCode())
                .montantEnvoye(transfer.getSentAmount())
                .deviseSource(transfer.getSourceCurrency())
                .montantRecu(transfer.getReceivedAmount())
                .deviseCible(transfer.getDestinationCurrency())
                .frais(transfer.getFees())
                .statut(transfer.getStatus().name())
                .dateEnvoi(transfer.getCreatedAt())
                .datePaiement(transfer.getPaymentDate())
                .nomBeneficiaire(transfer.getBeneficiaryName())
                .telephoneBeneficiaire(transfer.getBeneficiaryPhone())
                .paysBeneficiaire(transfer.getDestinationCountry())
                .nomAgentSaisie(nomAgentSaisie)
                .nomAgentPaiement(nomAgentPaiement)
                .build();
    }
}
