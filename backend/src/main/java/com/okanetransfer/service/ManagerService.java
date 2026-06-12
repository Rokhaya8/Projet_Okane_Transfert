package com.okanetransfer.service;

import com.okanetransfer.dto.*;
import com.okanetransfer.dto.request.AddAgentRequest;
import com.okanetransfer.dto.request.ChangePasswordRequest;
import com.okanetransfer.dto.request.UpdateAgentRequest;
import com.okanetransfer.entity.*;
import com.okanetransfer.enums.CashDrawerStatus;
import com.okanetransfer.enums.ReportPeriod;
import com.okanetransfer.enums.SensitiveOperationStatus;
import com.okanetransfer.exception.BusinessException;
import com.okanetransfer.exception.ResourceNotFoundException;
import com.okanetransfer.repository.*;
import com.okanetransfer.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerService {

    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final TransferRepository transferRepository;
    private final CashDrawerRepository cashDrawerRepository;
    private final SensitiveOperationRepository sensitiveOperationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> getAgents() {
        Agency agency = resolveManagerAgency();
        return userRepository.findByAgencyIdAndRole(agency.getId(), User.Role.ROLE_AGENT)
                .stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    public UserDTO addAgent(AddAgentRequest request) {
        Agency agency = resolveManagerAgency();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Un utilisateur avec cet email existe deja");
        }

        User agent = new User();
        agent.setFullName(request.getFullName());
        agent.setEmail(request.getEmail());
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agent.setPhone(request.getPhone());
        agent.setRole(User.Role.ROLE_AGENT);
        agent.setAgency(agency);
        agent.setActive(true);

        User saved = userRepository.save(agent);
        logAudit("ADD_AGENT", "User", saved.getId(), "Agent ajoute: " + saved.getEmail());
        return UserDTO.fromEntity(saved);
    }

    public UserDTO suspendAgent(Long agentId) {
        Agency agency = resolveManagerAgency();
        User agent = getAgencyAgent(agentId, agency);

        agent.setActive(false);
        User saved = userRepository.save(agent);
        logAudit("SUSPEND_AGENT", "User", saved.getId(), "Agent suspendu: " + saved.getEmail());
        return UserDTO.fromEntity(saved);
    }

    public UserDTO activateAgent(Long agentId) {
        Agency agency = resolveManagerAgency();
        User agent = getAgencyAgent(agentId, agency);

        agent.setActive(true);
        User saved = userRepository.save(agent);
        logAudit("ACTIVATE_AGENT", "User", saved.getId(), "Agent active: " + saved.getEmail());
        return UserDTO.fromEntity(saved);
    }

    public UserDTO updateAgent(Long agentId, UpdateAgentRequest request) {
        Agency agency = resolveManagerAgency();
        User agent = getAgencyAgent(agentId, agency);

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(agent.getId()))
                .ifPresent(existing -> {
                    throw new BusinessException("Un utilisateur avec cet email existe deja");
                });

        agent.setFullName(request.getFullName());
        agent.setEmail(request.getEmail());
        agent.setPhone(request.getPhone());

        User saved = userRepository.save(agent);
        logAudit("UPDATE_AGENT", "User", saved.getId(), "Agent modifie: " + saved.getEmail());
        return UserDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public AgentDetailDTO getAgentDetail(Long agentId) {
        Agency agency = resolveManagerAgency();
        User agent = getAgencyAgent(agentId, agency);
        List<Transfer> transfers = transferRepository.findByAgentIdOrderByCreatedAtDesc(agent.getId());

        long paidTransfers = transfers.stream()
                .filter(t -> t.getStatus() == Transfer.TransferStatus.PAID)
                .count();
        long pendingTransfers = transfers.stream()
                .filter(t -> t.getStatus() == Transfer.TransferStatus.PENDING)
                .count();
        BigDecimal totalAmount = transfers.stream()
                .map(Transfer::getAmountSent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AgentDetailDTO.fromEntity(agent, transfers.size(), paidTransfers, pendingTransfers, totalAmount);
    }

    @Transactional(readOnly = true)
    public List<SensitiveOperationDTO> getPendingValidations() {
        Agency agency = resolveManagerAgency();
        return sensitiveOperationRepository
                .findByAgencyIdAndStatusOrderByCreatedAtDesc(agency.getId(), SensitiveOperationStatus.PENDING)
                .stream()
                .map(SensitiveOperationDTO::fromEntity)
                .toList();
    }

    public SensitiveOperationDTO approveTransfer(Long operationId) {
        Agency agency = resolveManagerAgency();
        User manager = getCurrentManager();

        SensitiveOperation operation = sensitiveOperationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation introuvable"));

        validateOperationBelongsToAgency(operation, agency);

        if (operation.getStatus() != SensitiveOperationStatus.PENDING) {
            throw new BusinessException("Cette operation a deja ete traitee");
        }

        operation.setStatus(SensitiveOperationStatus.APPROVED);
        operation.setProcessedBy(manager);
        operation.setProcessedAt(LocalDateTime.now());

        if (operation.getTransfer() != null) {
            Transfer transfer = operation.getTransfer();
            if (transfer.getStatus() == Transfer.TransferStatus.PENDING) {
                transfer.setStatus(Transfer.TransferStatus.PENDING);
            }
        }

        SensitiveOperation saved = sensitiveOperationRepository.save(operation);
        logAudit("APPROVE_TRANSFER", "SensitiveOperation", saved.getId(), "Validation approuvee");
        return SensitiveOperationDTO.fromEntity(saved);
    }

    public SensitiveOperationDTO rejectTransfer(Long operationId, String reason) {
        Agency agency = resolveManagerAgency();
        User manager = getCurrentManager();

        SensitiveOperation operation = sensitiveOperationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation introuvable"));

        validateOperationBelongsToAgency(operation, agency);

        if (operation.getStatus() != SensitiveOperationStatus.PENDING) {
            throw new BusinessException("Cette operation a deja ete traitee");
        }

        operation.setStatus(SensitiveOperationStatus.REJECTED);
        operation.setProcessedBy(manager);
        operation.setProcessedAt(LocalDateTime.now());
        operation.setRejectionReason(reason);

        if (operation.getTransfer() != null) {
            operation.getTransfer().setStatus(Transfer.TransferStatus.CANCELLED);
        }

        SensitiveOperation saved = sensitiveOperationRepository.save(operation);
        logAudit("REJECT_TRANSFER", "SensitiveOperation", saved.getId(), "Validation rejetee: " + reason);
        return SensitiveOperationDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public AgencyReportDTO getAgencyReport(ReportPeriod period) {
        Agency agency = resolveManagerAgency();
        LocalDateTime[] range = resolvePeriodRange(period);
        List<Transfer> transfers = transferRepository.findAgencyTransfersInPeriod(agency.getId(), range[0], range[1]);

        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        long paidCount = 0;
        long pendingCount = 0;
        long cancelledCount = 0;

        for (Transfer transfer : transfers) {
            totalVolume = totalVolume.add(transfer.getAmountSent());
            totalFees = totalFees.add(transfer.getFees());
            totalCommission = totalCommission.add(transfer.getCommissionAgency());
            switch (transfer.getStatus()) {
                case PAID -> paidCount++;
                case PENDING -> pendingCount++;
                case CANCELLED, EXPIRED -> cancelledCount++;
            }
        }

        return AgencyReportDTO.builder()
                .agencyId(agency.getId())
                .agencyName(agency.getName())
                .period(period)
                .periodStart(range[0])
                .periodEnd(range[1])
                .transactionCount(transfers.size())
                .paidCount(paidCount)
                .pendingCount(pendingCount)
                .cancelledCount(cancelledCount)
                .totalVolume(totalVolume)
                .totalFees(totalFees)
                .totalCommissionAgency(totalCommission)
                .totalRevenue(totalFees)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TransferDTO> getTransactions(Transfer.TransferStatus status, LocalDate from, LocalDate to) {
        Agency agency = resolveManagerAgency();
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.now();

        return transferRepository.findAgencyTransactions(agency.getId(), status, fromDateTime, toDateTime)
                .stream()
                .map(TransferDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OperationDTO> getOperations(Transfer.TransferStatus status, LocalDate from, LocalDate to) {
        Agency agency = resolveManagerAgency();
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.now();

        return transferRepository.findAgencyTransactions(agency.getId(), status, fromDateTime, toDateTime)
                .stream()
                .map(OperationDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public OperationDetailDTO getOperationDetail(Long transferId) {
        Agency agency = resolveManagerAgency();
        Transfer transfer = transferRepository.findAgencyTransferById(agency.getId(), transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation introuvable"));
        return OperationDetailDTO.fromEntity(transfer);
    }

    @Transactional(readOnly = true)
    public AgencyPerformanceDTO getPerformance() {
        Agency agency = resolveManagerAgency();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Transfer> monthlyTransfers = transferRepository.findAgencyTransfersInPeriod(agency.getId(), monthStart, now);

        BigDecimal monthlyVolume = BigDecimal.ZERO;
        BigDecimal monthlyFees = BigDecimal.ZERO;
        long paidCount = 0;

        for (Transfer transfer : monthlyTransfers) {
            monthlyVolume = monthlyVolume.add(transfer.getAmountSent());
            monthlyFees = monthlyFees.add(transfer.getFees());
            if (transfer.getStatus() == Transfer.TransferStatus.PAID) {
                paidCount++;
            }
        }

        BigDecimal averageAmount = monthlyTransfers.isEmpty()
                ? BigDecimal.ZERO
                : monthlyVolume.divide(BigDecimal.valueOf(monthlyTransfers.size()), 2, RoundingMode.HALF_UP);

        double successRate = monthlyTransfers.isEmpty()
                ? 0.0
                : (paidCount * 100.0) / monthlyTransfers.size();

        return AgencyPerformanceDTO.builder()
                .agencyId(agency.getId())
                .agencyName(agency.getName())
                .activeAgents(userRepository.countByAgencyIdAndRoleAndActiveTrue(agency.getId(), User.Role.ROLE_AGENT))
                .openCashDrawers(cashDrawerRepository.countByAgencyIdAndStatus(agency.getId(), CashDrawerStatus.OPEN))
                .pendingValidations(sensitiveOperationRepository.countByAgencyIdAndStatus(
                        agency.getId(), SensitiveOperationStatus.PENDING))
                .transactionsThisMonth(monthlyTransfers.size())
                .averageTransactionAmount(averageAmount)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .monthlyVolume(monthlyVolume)
                .monthlyFees(monthlyFees)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AgentPerformanceDTO> getAgentPerformance(LocalDate from, LocalDate to) {
        Agency agency = resolveManagerAgency();
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<User> agents = userRepository.findByAgencyIdAndRole(agency.getId(), User.Role.ROLE_AGENT);
        List<Transfer> transfers = transferRepository.findAgencyTransfersInPeriod(agency.getId(), fromDateTime, toDateTime);
        Map<Long, List<Transfer>> transfersByAgent = transfers.stream()
                .filter(transfer -> transfer.getAgent() != null)
                .collect(Collectors.groupingBy(
                        transfer -> transfer.getAgent().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return agents.stream()
                .map(agent -> buildAgentPerformance(agent, transfersByAgent.getOrDefault(agent.getId(), List.of())))
                .sorted(Comparator.comparing(AgentPerformanceDTO::getTotalOperations).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public ManagerProfileDTO getProfile() {
        User manager = getCurrentManager();
        Agency agency = resolveManagerAgency();
        return ManagerProfileDTO.fromEntity(manager, agency);
    }

    public ManagerProfileDTO updateProfile(UpdateAgentRequest request) {
        User manager = getCurrentManager();
        Agency agency = resolveManagerAgency();

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(manager.getId()))
                .ifPresent(existing -> {
                    throw new BusinessException("Un utilisateur avec cet email existe deja");
                });

        manager.setFullName(request.getFullName());
        manager.setEmail(request.getEmail());
        manager.setPhone(request.getPhone());
        User saved = userRepository.save(manager);
        logAudit("UPDATE_MANAGER_PROFILE", "User", saved.getId(), "Profil manager modifie");
        return ManagerProfileDTO.fromEntity(saved, agency);
    }

    public void changePassword(ChangePasswordRequest request) {
        User manager = getCurrentManager();
        if (!passwordEncoder.matches(request.getCurrentPassword(), manager.getPassword())) {
            throw new BusinessException("Mot de passe actuel incorrect");
        }

        manager.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(manager);
        logAudit("CHANGE_MANAGER_PASSWORD", "User", manager.getId(), "Mot de passe manager modifie");
    }

    @Transactional(readOnly = true)
    public byte[] exportReportCsv(LocalDate from, LocalDate to) {
        Agency agency = resolveManagerAgency();
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.now();
        List<Transfer> transfers = transferRepository.findAgencyTransfersInPeriod(agency.getId(), fromDateTime, toDateTime);

        StringBuilder csv = new StringBuilder();
        csv.append("reference,agent,expediteur,beneficiaire,montant_envoye,devise_envoyee,montant_recu,devise_recue,date,statut\n");
        for (Transfer transfer : transfers) {
            csv.append(csv(transfer.getReferenceCode())).append(',')
                    .append(csv(transfer.getAgent() != null ? transfer.getAgent().getFullName() : null)).append(',')
                    .append(csv(transfer.getClient() != null ? transfer.getClient().getFullName() : null)).append(',')
                    .append(csv(transfer.getBeneficiary() != null ? transfer.getBeneficiary().getFullName() : null)).append(',')
                    .append(transfer.getAmountSent()).append(',')
                    .append(csv(transfer.getCorridor() != null && transfer.getCorridor().getSourceCurrency() != null
                            ? transfer.getCorridor().getSourceCurrency().getCode() : null)).append(',')
                    .append(transfer.getAmountReceived()).append(',')
                    .append(csv(transfer.getCorridor() != null && transfer.getCorridor().getDestinationCurrency() != null
                            ? transfer.getCorridor().getDestinationCurrency().getCode() : null)).append(',')
                    .append(csv(transfer.getCreatedAt() != null ? transfer.getCreatedAt().toString() : null)).append(',')
                    .append(csv(transfer.getStatus() != null ? transfer.getStatus().name() : null)).append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] exportReportPdf(LocalDate from, LocalDate to) {
        Agency agency = resolveManagerAgency();
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : LocalDateTime.now();
        List<Transfer> transfers = transferRepository.findAgencyTransfersInPeriod(agency.getId(), fromDateTime, toDateTime);

        BigDecimal total = transfers.stream()
                .map(Transfer::getAmountSent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long paid = transfers.stream()
                .filter(transfer -> transfer.getStatus() == Transfer.TransferStatus.PAID)
                .count();

        String content = "Okane Transfer - Rapport agence\n"
                + "Agence: " + agency.getName() + "\n"
                + "Transactions: " + transfers.size() + "\n"
                + "Montant total: " + total + "\n"
                + "Paiements: " + paid + "\n";
        return buildSimplePdf(content);
    }

    @Transactional(readOnly = true)
    public List<CashDrawerDTO> getCashDrawers() {
        Agency agency = resolveManagerAgency();
        return cashDrawerRepository.findByAgencyIdOrderByOpenedAtDesc(agency.getId())
                .stream()
                .map(CashDrawerDTO::fromEntity)
                .toList();
    }

    private Agency resolveManagerAgency() {
        User manager = getCurrentManager();
        return agencyRepository.findByManagerId(manager.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aucune agence associee a ce manager"));
    }

    private User getCurrentManager() {
        String email = SecurityUtils.getCurrentUserEmail();
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Manager introuvable"));
        if (manager.getRole() != User.Role.ROLE_MANAGER) {
            throw new BusinessException("Acces reserve aux managers");
        }
        return manager;
    }

    private User getAgencyAgent(Long agentId, Agency agency) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable"));

        if (agent.getRole() != User.Role.ROLE_AGENT
                || agent.getAgency() == null
                || !agent.getAgency().getId().equals(agency.getId())) {
            throw new BusinessException("Cet agent n'appartient pas a votre agence");
        }

        return agent;
    }

    private AgentPerformanceDTO buildAgentPerformance(User agent, List<Transfer> transfers) {
        long paid = transfers.stream()
                .filter(transfer -> transfer.getStatus() == Transfer.TransferStatus.PAID)
                .count();
        long pending = transfers.stream()
                .filter(transfer -> transfer.getStatus() == Transfer.TransferStatus.PENDING)
                .count();
        BigDecimal totalAmount = transfers.stream()
                .map(Transfer::getAmountSent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AgentPerformanceDTO.builder()
                .agentId(agent.getId())
                .agentName(agent.getFullName())
                .totalOperations(transfers.size())
                .paidOperations(paid)
                .pendingOperations(pending)
                .totalAmount(totalAmount)
                .build();
    }

    private void validateOperationBelongsToAgency(SensitiveOperation operation, Agency agency) {
        if (operation.getAgency() == null || !operation.getAgency().getId().equals(agency.getId())) {
            throw new BusinessException("Cette operation n'appartient pas a votre agence");
        }
    }

    private LocalDateTime[] resolvePeriodRange(ReportPeriod period) {
        LocalDate today = LocalDate.now();
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (period) {
            case DAY -> start = today.atStartOfDay();
            case WEEK -> start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            case MONTH -> start = today.withDayOfMonth(1).atStartOfDay();
            default -> start = today.atStartOfDay();
        }

        return new LocalDateTime[]{start, end};
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private byte[] buildSimplePdf(String text) {
        String escapedText = text.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "")
                .replace("\n", ") Tj T* (");
        String stream = "BT /F1 12 Tf 50 780 Td 16 TL (" + escapedText + ") Tj ET";
        String[] objects = {
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Length " + stream.getBytes(StandardCharsets.UTF_8).length + " >>\nstream\n" + stream + "\nendstream"
        };

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        int[] offsets = new int[objects.length + 1];
        for (int i = 0; i < objects.length; i++) {
            offsets[i + 1] = pdf.toString().getBytes(StandardCharsets.UTF_8).length;
            pdf.append(i + 1).append(" 0 obj\n").append(objects[i]).append("\nendobj\n");
        }

        int xref = pdf.toString().getBytes(StandardCharsets.UTF_8).length;
        pdf.append("xref\n0 ").append(objects.length + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (int i = 1; i <= objects.length; i++) {
            pdf.append(String.format("%010d 00000 n \n", offsets[i]));
        }
        pdf.append("trailer\n<< /Size ").append(objects.length + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xref).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void logAudit(String action, String entityType, Long entityId, String details) {
        User manager = getCurrentManager();
        AuditLog log = new AuditLog();
        log.setUser(manager);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setIpAddress("127.0.0.1");
        auditLogRepository.save(log);
    }
}
