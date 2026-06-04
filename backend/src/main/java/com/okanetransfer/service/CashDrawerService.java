package com.okanetransfer.service;

import com.okanetransfer.dto.DiscrepancyRequest;
import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.entity.User;
import com.okanetransfer.repository.CashDrawerRepository;
import com.okanetransfer.repository.TransferRepository;
import com.okanetransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CashDrawerService {

    @Autowired
    private CashDrawerRepository cashDrawerRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private UserRepository userRepository;

    public CashDrawer openCashDrawer(Long agentId, Long agencyId, BigDecimal initialBalance) {
        // Fermer l'ancienne caisse si elle existe
        cashDrawerRepository.findByAgentId(agentId).ifPresent(existing -> {
            existing.setStatus(CashDrawer.CashDrawerStatus.CLOSED);
            cashDrawerRepository.save(existing);
        });

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent introuvable"));

        CashDrawer cashDrawer = new CashDrawer();
        cashDrawer.setAgent(agent);
        cashDrawer.setBalance(initialBalance);
        cashDrawer.setStatus(CashDrawer.CashDrawerStatus.OPEN);
        cashDrawer.setOpeningTime(LocalDateTime.now());
        return cashDrawerRepository.save(cashDrawer);
    }

    public Map<String, Object> closeCashDrawer(Long agentId, BigDecimal countedAmount) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

        BigDecimal systemBalance = cashDrawer.getBalance();
        BigDecimal difference = systemBalance.subtract(countedAmount);

        cashDrawer.setStatus(CashDrawer.CashDrawerStatus.CLOSED);
        cashDrawer.setClosingTime(LocalDateTime.now());
        cashDrawerRepository.save(cashDrawer);

        Map<String, Object> result = new HashMap<>();
        result.put("systemBalance", systemBalance);
        result.put("countedAmount", countedAmount);
        result.put("difference", difference);
        result.put("status", "CLOSED");
        result.put("hasDiscrepancy", difference.compareTo(BigDecimal.ZERO) != 0);
        return result;
    }

    public BigDecimal getCurrentBalance(Long agentId) {
        return cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"))
                .getBalance();
    }

    public List<Transfer> getDayOperations(Long agentId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return transferRepository.findByAgentIdAndCreatedAtBetween(agentId, start, end);
    }

    public Map<String, Object> reportDiscrepancy(Long agentId, DiscrepancyRequest request) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

        BigDecimal difference = cashDrawer.getBalance().subtract(request.getCountedAmount());

        Map<String, Object> result = new HashMap<>();
        result.put("agentId", agentId);
        result.put("systemBalance", cashDrawer.getBalance());
        result.put("countedAmount", request.getCountedAmount());
        result.put("difference", difference);
        result.put("reason", request.getReason());
        result.put("reportedAt", LocalDateTime.now().toString());
        return result;
    }

    public List<CashDrawer> getAgencyCashDrawers(Long agencyId) {
        return cashDrawerRepository.findByAgencyId(agencyId);
    }
}
