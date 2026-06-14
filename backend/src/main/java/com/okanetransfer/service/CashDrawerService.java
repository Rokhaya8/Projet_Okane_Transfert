package com.okanetransfer.service;

import com.okanetransfer.entity.Agency;
import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.entity.User;
import com.okanetransfer.exception.BusinessException;
import com.okanetransfer.exception.ResourceNotFoundException;
import com.okanetransfer.repository.AgencyRepository;
import com.okanetransfer.repository.CashDrawerRepository;
import com.okanetransfer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CashDrawerService {

    private final CashDrawerRepository cashDrawerRepository;
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;

    public CashDrawerService(CashDrawerRepository cashDrawerRepository,
                              UserRepository userRepository,
                              AgencyRepository agencyRepository) {
        this.cashDrawerRepository = cashDrawerRepository;
        this.userRepository = userRepository;
        this.agencyRepository = agencyRepository;
    }

    public CashDrawer openCashDrawer(Long agentId, Long agencyId, BigDecimal initialBalance) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable"));
        Agency agency = agencyRepository.findById(agencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence introuvable"));

        cashDrawerRepository.findByAgentId(agentId).ifPresent(existing -> {
            if (existing.getStatus() == CashDrawer.CashDrawerStatus.OPEN) {
                throw new BusinessException("Une caisse est deja ouverte pour cet agent");
            }
        });

        CashDrawer cashDrawer = new CashDrawer();
        cashDrawer.setAgent(agent);
        cashDrawer.setAgency(agency);
        cashDrawer.setBalance(initialBalance);
        cashDrawer.setStatus(CashDrawer.CashDrawerStatus.OPEN);
        cashDrawer.setOpeningTime(LocalDateTime.now());
        return cashDrawerRepository.save(cashDrawer);
    }

    public CashDrawer closeCashDrawer(Long agentId, BigDecimal countedAmount) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Caisse introuvable"));

        BigDecimal difference = cashDrawer.getBalance().subtract(countedAmount);
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            System.out.println("Ecart detecte : " + difference + " MAD");
        }

        cashDrawer.setStatus(CashDrawer.CashDrawerStatus.CLOSED);
        cashDrawer.setClosingTime(LocalDateTime.now());
        return cashDrawerRepository.save(cashDrawer);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentBalance(Long agentId) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Caisse introuvable"));
        return cashDrawer.getBalance();
    }

    @Transactional(readOnly = true)
    public List<CashDrawer> getAgencyCashDrawers(Long agencyId) {
        return cashDrawerRepository.findByAgencyId(agencyId);
    }
}
