package com.okanetransfer.service;

import com.okanetransfer.dto.*;
import com.okanetransfer.entity.*;
import com.okanetransfer.exception.BusinessException;
import com.okanetransfer.exception.ResourceNotFoundException;
import com.okanetransfer.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AgentCashSessionService {

    private final AgentCashSessionRepository cashSessionRepository;
    private final CashOperationRepository cashOperationRepository;
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;

    public AgentCashSessionService(
            AgentCashSessionRepository cashSessionRepository,
            CashOperationRepository cashOperationRepository,
            UserRepository userRepository,
            AgencyRepository agencyRepository
    ) {
        this.cashSessionRepository = cashSessionRepository;
        this.cashOperationRepository = cashOperationRepository;
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
    }

    public CashSessionResponse openSession(OpenCashSessionRequest request) {
        if (cashSessionRepository.existsByAgentIdAndStatus(request.agentId(), CashSessionStatus.OPEN)) {
            throw new BusinessException("Une caisse est deja ouverte pour cet agent");
        }

        User agent = userRepository.findById(request.agentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable"));
        if (agent.getRole() != User.Role.ROLE_AGENT) {
            throw new BusinessException("L'utilisateur selectionne n'est pas un agent");
        }

        Agency agency = agencyRepository.findById(request.agencyId())
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));

        AgentCashSession session = new AgentCashSession();
        session.setAgent(agent);
        session.setAgency(agency);
        session.setOpeningBalance(request.openingBalance());
        session.setCurrentBalance(request.openingBalance());
        session.setOpenedAt(LocalDateTime.now());
        session.setStatus(CashSessionStatus.OPEN);

        AgentCashSession saved = cashSessionRepository.save(session);
        recordOperation(saved, CashOperationType.OPENING, request.openingBalance(),
                BigDecimal.ZERO, request.openingBalance(), "OPEN-" + saved.getId(), null);

        return toSessionResponse(saved);
    }

    @Transactional(readOnly = true)
    public CashSessionResponse getCurrentSession(Long agentId) {
        return cashSessionRepository.findByAgentIdAndStatus(agentId, CashSessionStatus.OPEN)
                .map(this::toSessionResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune caisse ouverte pour cet agent"));
    }

    @Transactional(readOnly = true)
    public List<CashSessionResponse> getAgentSessions(Long agentId) {
        return cashSessionRepository.findByAgentIdOrderByOpenedAtDesc(agentId)
                .stream()
                .map(this::toSessionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CashOperationResponse> getSessionOperations(Long sessionId) {
        if (!cashSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Session de caisse introuvable");
        }
        return cashOperationRepository.findByCashSessionIdOrderByOperationDateDesc(sessionId)
                .stream()
                .map(this::toOperationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CashOperationResponse> getAgentOperations(Long agentId) {
        return cashOperationRepository.findByCashSessionAgentIdOrderByOperationDateDesc(agentId)
                .stream()
                .map(this::toOperationResponse)
                .toList();
    }

    public CashSessionResponse adjustCurrentSession(Long agentId, CashAdjustmentRequest request) {
        AgentCashSession session = getOpenSessionForUpdate(agentId);
        BigDecimal before = session.getCurrentBalance();
        BigDecimal after = before.add(request.amount());

        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("L'ajustement rendrait le solde de caisse negatif");
        }

        session.setCurrentBalance(after);
        AgentCashSession saved = cashSessionRepository.save(session);
        recordOperation(saved, CashOperationType.ADJUSTMENT, request.amount(), before, after, request.reference(), null);
        return toSessionResponse(saved);
    }

    public CashSessionResponse closeCurrentSession(Long agentId, CloseCashSessionRequest request) {
        AgentCashSession session = getOpenSessionForUpdate(agentId);
        BigDecimal theoreticalBalance = session.getCurrentBalance();
        BigDecimal discrepancy = request.countedAmount().subtract(theoreticalBalance);

        session.setClosingBalance(theoreticalBalance);
        session.setCountedAmount(request.countedAmount());
        session.setDiscrepancyAmount(discrepancy);
        session.setClosedAt(LocalDateTime.now());
        session.setStatus(CashSessionStatus.CLOSED);

        AgentCashSession saved = cashSessionRepository.save(session);
        recordOperation(saved, CashOperationType.CLOSING, request.countedAmount(),
                theoreticalBalance, theoreticalBalance, "CLOSE-" + saved.getId(), null);

        return toSessionResponse(saved);
    }

    public AgentCashSession applyTransferSent(Long agentId, Transfer transfer) {
        return applyOperation(agentId, CashOperationType.TRANSFER_SENT, transfer.getAmountSent(), true,
                transfer.getReferenceCode(), transfer);
    }

    public AgentCashSession applyTransferPaid(Long agentId, Transfer transfer) {
        return applyOperation(agentId, CashOperationType.TRANSFER_PAID, transfer.getAmountReceived(), false,
                transfer.getReferenceCode(), transfer);
    }

    public AgentCashSession getOpenSessionForUpdate(Long agentId) {
        return cashSessionRepository.findWithLockByAgentIdAndStatus(agentId, CashSessionStatus.OPEN)
                .orElseThrow(() -> new BusinessException("L'agent doit ouvrir sa caisse avant cette operation"));
    }

    private AgentCashSession applyOperation(
            Long agentId,
            CashOperationType type,
            BigDecimal amount,
            boolean credit,
            String reference,
            Transfer transfer
    ) {
        AgentCashSession session = getOpenSessionForUpdate(agentId);
        BigDecimal before = session.getCurrentBalance();
        BigDecimal after = credit ? before.add(amount) : before.subtract(amount);

        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Solde de caisse insuffisant");
        }

        session.setCurrentBalance(after);
        AgentCashSession saved = cashSessionRepository.save(session);
        recordOperation(saved, type, amount, before, after, reference, transfer);
        return saved;
    }

    private void recordOperation(
            AgentCashSession session,
            CashOperationType type,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String reference,
            Transfer transfer
    ) {
        CashOperation operation = new CashOperation();
        operation.setCashSession(session);
        operation.setOperationType(type);
        operation.setAmount(amount);
        operation.setBalanceBefore(balanceBefore);
        operation.setBalanceAfter(balanceAfter);
        operation.setOperationDate(LocalDateTime.now());
        operation.setReference(reference);
        operation.setTransfer(transfer);
        cashOperationRepository.save(operation);
    }

    private CashSessionResponse toSessionResponse(AgentCashSession session) {
        return new CashSessionResponse(
                session.getId(),
                session.getAgent().getId(),
                session.getAgent().getFullName(),
                session.getAgency().getId(),
                session.getAgency().getName(),
                session.getOpeningBalance(),
                session.getCurrentBalance(),
                session.getClosingBalance(),
                session.getCountedAmount(),
                session.getDiscrepancyAmount(),
                session.getOpenedAt(),
                session.getClosedAt(),
                session.getStatus()
        );
    }

    private CashOperationResponse toOperationResponse(CashOperation operation) {
        Long transferId = operation.getTransfer() == null ? null : operation.getTransfer().getId();
        return new CashOperationResponse(
                operation.getId(),
                operation.getCashSession().getId(),
                operation.getOperationType(),
                operation.getAmount(),
                operation.getBalanceBefore(),
                operation.getBalanceAfter(),
                operation.getOperationDate(),
                operation.getReference(),
                transferId
        );
    }
}
