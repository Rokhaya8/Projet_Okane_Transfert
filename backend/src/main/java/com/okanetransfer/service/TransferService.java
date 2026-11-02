package com.okanetransfer.service;

import com.okanetransfer.dto.*;
import com.okanetransfer.dto.response.TransferResponse;
import com.okanetransfer.entity.*;
import com.okanetransfer.exception.BusinessException;
import com.okanetransfer.exception.ResourceNotFoundException;
import com.okanetransfer.repository.AgentRepository;
import com.okanetransfer.repository.TransferPaymentRepository;
import com.okanetransfer.repository.TransferRepository;
import com.okanetransfer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferPaymentRepository transferPaymentRepository;
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final FeeService feeService;
    private final ExchangeRateService exchangeRateService;
    private final AgentCashSessionService cashSessionService;

    public TransferService(
            TransferRepository transferRepository,
            TransferPaymentRepository transferPaymentRepository,
            AgentRepository agentRepository,
            UserRepository userRepository,
            FeeService feeService,
            ExchangeRateService exchangeRateService,
            AgentCashSessionService cashSessionService
    ) {
        this.transferRepository = transferRepository;
        this.transferPaymentRepository = transferPaymentRepository;
        this.agentRepository = agentRepository;
        this.userRepository = userRepository;
        this.feeService = feeService;
        this.exchangeRateService = exchangeRateService;
        this.cashSessionService = cashSessionService;
    }

    public Transfer registerTransfer(Transfer transfer, Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable"));
        AgentCashSession session = cashSessionService.getOpenSessionForUpdate(agentId);

        TransferCorridor corridor = feeService.findCorridor(
                transfer.getCorridor().getSourceCountry(),
                transfer.getBeneficiary().getCountry()
        );
        transfer.setCorridor(corridor);

        BigDecimal fees = feeService.calculateFees(corridor.getId(), transfer.getAmountSent());
        transfer.setFees(fees);

        BigDecimal amountReceived = exchangeRateService.convert(
                corridor.getDestinationCurrency().getId(),
                transfer.getAmountSent()
        );
        transfer.setAmountReceived(amountReceived);

        transfer.setReferenceCode(generateUniqueReferenceCode());
        transfer.setStatus(Transfer.TransferStatus.EN_ATTENTE);
        transfer.setExpiryDate(LocalDateTime.now().plusDays(7));
        transfer.setAgent(agent);
        transfer.setAgency(session.getAgency());

        Transfer savedTransfer = transferRepository.save(transfer);
        cashSessionService.applyTransferSent(agentId, savedTransfer);
        return savedTransfer;
    }

    public TransferPaymentResponse payTransfer(PayTransferRequest request) {
        Transfer transfer = transferRepository.findByReferenceCode(request.referenceCode())
                .orElseThrow(() -> new ResourceNotFoundException("Transfert introuvable"));

        validatePayableTransfer(transfer);

        User agent = userRepository.findById(request.agentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable"));

        AgentCashSession session = cashSessionService.applyTransferPaid(request.agentId(), transfer);

        transfer.setStatus(Transfer.TransferStatus.PAYE);
        transfer.setPaidAt(LocalDateTime.now());
        transfer.setPayingAgent(agent);
        transfer.setPayingAgency(session.getAgency());
        Transfer paidTransfer = transferRepository.save(transfer);

        TransferPayment payment = new TransferPayment();
        payment.setTransfer(paidTransfer);
        payment.setAgent(agent);
        payment.setAgency(session.getAgency());
        payment.setBeneficiaryIdentityNumber(request.beneficiaryIdentityNumber());
        payment.setPaidAmount(paidTransfer.getAmountReceived());
        payment.setPaidAt(paidTransfer.getPaidAt());
        payment.setReceiptNumber(generateReceiptNumber());

        return toPaymentResponse(transferPaymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public Optional<Transfer> findByReferenceCode(String referenceCode) {
        return transferRepository.findByReferenceCode(referenceCode);
    }

    @Transactional(readOnly = true)
    public TransferSearchResponse searchPayableByReferenceCode(String referenceCode) {
        Transfer transfer = transferRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Transfert introuvable"));
        return toSearchResponse(transfer);
    }

    @Transactional(readOnly = true)
    public List<TransferSearchResponse> searchPayableByBeneficiaryPhone(String phone) {
        return transferRepository.findByBeneficiaryPhone(phone)
                .stream()
                .map(this::toSearchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransferPaymentResponse> getPaymentHistory(Long agentId) {
        return transferPaymentRepository.findByAgentIdOrderByPaidAtDesc(agentId)
                .stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceipt(Long paymentId) {
        TransferPayment payment = transferPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable"));
        Transfer transfer = payment.getTransfer();
        Beneficiary beneficiary = transfer.getBeneficiary();
        return new ReceiptResponse(
                payment.getReceiptNumber(),
                transfer.getReferenceCode(),
                payment.getAgent().getFullName(),
                payment.getAgency().getName(),
                beneficiary.getFullName(),
                beneficiary.getPhone(),
                payment.getBeneficiaryIdentityNumber(),
                payment.getPaidAmount(),
                payment.getPaidAt()
        );
    }

    @Transactional(readOnly = true)
    public List<Transfer> getAgentTransfers(Long agentId) {
        return transferRepository.findByAgentId(agentId);
    }

    // ===== POUR L'ADMIN DASHBOARD =====
    @Transactional(readOnly = true)
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
                .beneficiaryName(t.getBeneficiary() != null ? t.getBeneficiary().getFullName() : "-")
                .build();
    }

    private void validatePayableTransfer(Transfer transfer) {
        if (transfer.getStatus() != Transfer.TransferStatus.EN_ATTENTE) {
            throw new BusinessException("Ce transfert ne peut pas etre paye");
        }
        if (transfer.getExpiryDate() != null && transfer.getExpiryDate().isBefore(LocalDateTime.now())) {
            transfer.setStatus(Transfer.TransferStatus.EXPIRE);
            transferRepository.save(transfer);
            throw new BusinessException("Ce transfert est expire");
        }
    }

    private TransferSearchResponse toSearchResponse(Transfer transfer) {
        Beneficiary beneficiary = transfer.getBeneficiary();
        return new TransferSearchResponse(
                transfer.getId(),
                transfer.getReferenceCode(),
                transfer.getAmountSent(),
                transfer.getAmountReceived(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getPaidAt(),
                transfer.getExpiryDate(),
                beneficiary == null ? null : beneficiary.getFullName(),
                beneficiary == null ? null : beneficiary.getPhone(),
                beneficiary == null ? null : beneficiary.getCountry(),
                isPayable(transfer)
        );
    }

    private boolean isPayable(Transfer transfer) {
        return transfer.getStatus() == Transfer.TransferStatus.EN_ATTENTE
                && (transfer.getExpiryDate() == null || !transfer.getExpiryDate().isBefore(LocalDateTime.now()));
    }

    private TransferPaymentResponse toPaymentResponse(TransferPayment payment) {
        Transfer transfer = payment.getTransfer();
        Beneficiary beneficiary = transfer.getBeneficiary();
        return new TransferPaymentResponse(
                payment.getId(),
                transfer.getId(),
                transfer.getReferenceCode(),
                payment.getAgent().getId(),
                payment.getAgent().getFullName(),
                payment.getAgency().getId(),
                payment.getAgency().getName(),
                beneficiary == null ? null : beneficiary.getFullName(),
                payment.getBeneficiaryIdentityNumber(),
                payment.getPaidAmount(),
                payment.getPaidAt(),
                payment.getReceiptNumber()
        );
    }

    private String generateUniqueReferenceCode() {
        String code;
        do {
            code = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (transferRepository.findByReferenceCode(code).isPresent());
        return code;
    }

    private String generateReceiptNumber() {
        return "RCT-" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }
}