package com.okanetransfer.service;

import com.okanetransfer.dto.*;
import com.okanetransfer.entity.*;
import com.okanetransfer.exception.CashDrawerAlreadyClosedException;
import com.okanetransfer.repository.CashDrawerRepository;
import com.okanetransfer.repository.CashOperationRepository;
import com.okanetransfer.repository.DiscrepancyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de caisse.
 * Gère le solde, les opérations, la clôture et les écarts de caisse.
 */
@Service
@RequiredArgsConstructor
public class CashService {

    private final CashDrawerRepository cashDrawerRepository;
    private final CashOperationRepository cashOperationRepository;
    private final DiscrepancyReportRepository discrepancyReportRepository;
    private final AuditService auditService;

    /**
     * Récupère le solde actuel de la caisse de l'agent.
     */
    public CashBalanceDTO getBalance(Long agentId) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new IllegalStateException("Aucune caisse trouvée pour cet agent"));

        // Récupérer la devise locale de l'agence (via le premier corridor ou configuration)
        String currency = "MAD"; // Devise par défaut, à adapter selon l'agence
        if (cashDrawer.getAgency() != null) {
            // TODO: Récupérer la devise de l'agence depuis la configuration
            currency = "MAD"; // Placeholder
        }

        return CashBalanceDTO.builder()
                .solde(cashDrawer.getBalance())
                .devise(currency)
                .build();
    }

    /**
     * Récupère les opérations de caisse pour une date donnée.
     * Si aucune date n'est fournie, retourne les opérations du jour.
     */
    public List<OperationCaisseDTO> getOperations(Long agentId, LocalDate date) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new IllegalStateException("Aucune caisse trouvée pour cet agent"));

        // Si aucune date n'est spécifiée, prendre la date du jour
        LocalDate effectiveDate = date != null ? date : LocalDate.now();
        
        LocalDateTime startOfDay = effectiveDate.atStartOfDay();
        LocalDateTime endOfDay = effectiveDate.atTime(LocalTime.MAX);

        List<CashOperation> operations = cashOperationRepository
                .findByCashDrawerIdAndOperationDateBetweenOrderByOperationDateDesc(
                        cashDrawer.getId(), startOfDay, endOfDay);

        return operations.stream()
                .map(this::mapToOperationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Clôture la caisse journalière.
     * Calcule le solde théorique, compare avec le solde réel et enregistre l'écart.
     */
    @Transactional
    public CashCloseResponseDTO closeCashDrawer(Long agentId, CashCloseRequestDTO request, String ipAddress) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new IllegalStateException("Aucune caisse trouvée pour cet agent"));

        // Vérifier que la caisse n'est pas déjà clôturée pour aujourd'hui
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        boolean alreadyClosed = cashOperationRepository.existsByCashDrawerIdAndTypeAndOperationDateBetween(
                cashDrawer.getId(),
                CashOperation.OperationType.CLOTURE,
                startOfDay,
                endOfDay
        );

        if (alreadyClosed) {
            throw new CashDrawerAlreadyClosedException("La caisse est déjà clôturée pour aujourd'hui");
        }

        // Calculer le solde théorique (solde actuel de la base de données)
        BigDecimal soldeTheorique = cashDrawer.getBalance();
        BigDecimal soldeReel = request.getSoldeReelSaisi();
        BigDecimal ecart = soldeReel.subtract(soldeTheorique);

        // Enregistrer l'opération de clôture
        CashOperation clotureOperation = new CashOperation();
        clotureOperation.setCashDrawer(cashDrawer);
        clotureOperation.setType(CashOperation.OperationType.CLOTURE);
        clotureOperation.setAmount(ecart);
        clotureOperation.setBalanceAfter(soldeReel);
        clotureOperation.setDescription("Clôture journalière - Écart: " + ecart);
        cashOperationRepository.save(clotureOperation);

        // Mettre à jour le solde de la caisse avec le solde réel
        cashDrawer.setBalance(soldeReel);
        cashDrawer.setClosingTime(LocalDateTime.now());
        cashDrawerRepository.save(cashDrawer);

        // Journaliser l'action
        auditService.logAction(
                agentId,
                "CLOSE_CASH_DRAWER",
                "CashDrawer",
                cashDrawer.getId(),
                String.format("Clôture - Théorique: %s, Réel: %s, Écart: %s", soldeTheorique, soldeReel, ecart),
                ipAddress
        );

        String message = ecart.compareTo(BigDecimal.ZERO) == 0
                ? "Clôture effectuée sans écart"
                : "Clôture effectuée avec écart";

        return CashCloseResponseDTO.builder()
                .soldeTheorique(soldeTheorique)
                .soldeReel(soldeReel)
                .ecart(ecart)
                .message(message)
                .build();
    }

    /**
     * Enregistre un signalement d'écart de caisse.
     */
    @Transactional
    public void reportDiscrepancy(Long agentId, DiscrepancyRequestDTO request, String ipAddress) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new IllegalStateException("Aucune caisse trouvée pour cet agent"));

        DiscrepancyReport report = new DiscrepancyReport();
        report.setAgentId(agentId);
        report.setCashDrawer(cashDrawer);
        report.setEcartConstate(request.getEcartConstate());
        report.setCommentaire(request.getCommentaire());
        discrepancyReportRepository.save(report);

        // Journaliser le signalement
        auditService.logAction(
                agentId,
                "REPORT_DISCREPANCY",
                "DiscrepancyReport",
                report.getId(),
                "Écart signalé: " + request.getEcartConstate() + " - " + request.getCommentaire(),
                ipAddress
        );
    }

    private OperationCaisseDTO mapToOperationDTO(CashOperation operation) {
        return OperationCaisseDTO.builder()
                .id(operation.getId())
                .type(operation.getType().name())
                .montant(operation.getAmount())
                .soldeApres(operation.getBalanceAfter())
                .date(operation.getOperationDate())
                .referenceTransfertId(operation.getTransfer() != null ? operation.getTransfer().getId() : null)
                .description(operation.getDescription())
                .build();
    }
}
