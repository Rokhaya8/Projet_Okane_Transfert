package com.okanetransfer.service;

import com.okanetransfer.dto.PayoutRequest;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.repository.CashDrawerRepository;
import com.okanetransfer.repository.TransferRepository;
import com.okanetransfer.entity.CashDrawer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public Transfer registerTransfer(Transfer transfer, Long agentId) {
        var corridor = feeService.findCorridor(
                transfer.getCorridor().getSourceCountry(),
                transfer.getBeneficiary().getCountry());

        BigDecimal fees = feeService.calculateFees(corridor.getId(), transfer.getAmountSent());
        transfer.setFees(fees);

        BigDecimal amountReceived = exchangeRateService.convert(
                corridor.getDestinationCurrency().getId(), transfer.getAmountSent());
        transfer.setAmountReceived(amountReceived);

        transfer.setReferenceCode(generateReferenceCode());
        transfer.setStatus(Transfer.TransferStatus.PENDING);
        transfer.setExpiryDate(LocalDateTime.now().plusDays(7));

        Transfer saved = transferRepository.save(transfer);
        updateCashDrawer(agentId, transfer.getAmountSent(), true);
        return saved;
    }

    public Transfer payout(Long transfertId, Long agentId, PayoutRequest request) {
        Transfer transfer = transferRepository.findById(transfertId)
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

        if (transfer.getStatus() != Transfer.TransferStatus.PENDING)
            throw new RuntimeException("Ce transfert ne peut pas être payé");

        if (transfer.getExpiryDate().isBefore(LocalDateTime.now())) {
            transfer.setStatus(Transfer.TransferStatus.EXPIRED);
            transferRepository.save(transfer);
            throw new RuntimeException("Ce transfert est expiré");
        }

        transfer.setStatus(Transfer.TransferStatus.PAID);
        transfer.setPaidAt(LocalDateTime.now());

        updateCashDrawer(agentId, transfer.getAmountReceived(), false);
        return transferRepository.save(transfer);
    }

    public Transfer payTransfer(String referenceCode, Long agentId) {
        Transfer transfer = transferRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new RuntimeException("Transfert introuvable"));

        if (transfer.getStatus() != Transfer.TransferStatus.PENDING)
            throw new RuntimeException("Ce transfert ne peut pas être payé");

        if (transfer.getExpiryDate().isBefore(LocalDateTime.now())) {
            transfer.setStatus(Transfer.TransferStatus.EXPIRED);
            transferRepository.save(transfer);
            throw new RuntimeException("Ce transfert est expiré");
        }

        transfer.setStatus(Transfer.TransferStatus.PAID);
        transfer.setPaidAt(LocalDateTime.now());
        updateCashDrawer(agentId, transfer.getAmountReceived(), false);
        return transferRepository.save(transfer);
    }

    public Page<Transfer> getHistory(Long agentId, int page, int size, String status,
                                      LocalDate startDate, LocalDate endDate) {
        Transfer.TransferStatus st = null;
        if (status != null && !status.isEmpty()) {
            st = Transfer.TransferStatus.valueOf(status);
        }
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (st == null) {
            return transferRepository.findAgentHistoryNoStatus(agentId, start, end, pageable);
        }
        return transferRepository.findAgentHistoryWithStatus(agentId, st, start, end, pageable);
    }

    public Optional<Transfer> findByReferenceCode(String referenceCode) {
        return transferRepository.findByReferenceCode(referenceCode);
    }

    public List<Transfer> findByBeneficiaryPhone(String phone) {
        return transferRepository.findByBeneficiaryPhone(phone);
    }

    public List<Transfer> getAgentTransfers(Long agentId) {
        return transferRepository.findByAgentId(agentId);
    }

    private String generateReferenceCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

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
