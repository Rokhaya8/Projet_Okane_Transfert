package com.okanetransfer.service;

import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.repository.CashDrawerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CashDrawerService {

    @Autowired
    private CashDrawerRepository cashDrawerRepository;

    // Ouvrir la caisse en début de journée
    public CashDrawer openCashDrawer(Long agentId, Long agencyId, BigDecimal initialBalance) {
        CashDrawer cashDrawer = new CashDrawer();
        cashDrawer.setBalance(initialBalance);
        cashDrawer.setStatus(CashDrawer.CashDrawerStatus.OPEN);
        cashDrawer.setOpeningTime(LocalDateTime.now());
        return cashDrawerRepository.save(cashDrawer);
    }

    // Clôturer la caisse en fin de journée
    public CashDrawer closeCashDrawer(Long agentId, BigDecimal countedAmount) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

        // Vérifier s'il y a un écart
        BigDecimal difference = cashDrawer.getBalance().subtract(countedAmount);
        if (difference.compareTo(BigDecimal.ZERO) != 0) {
            // Signaler l'écart
            System.out.println("Écart détecté : " + difference + " MAD");
        }

        cashDrawer.setStatus(CashDrawer.CashDrawerStatus.CLOSED);
        cashDrawer.setClosingTime(LocalDateTime.now());
        return cashDrawerRepository.save(cashDrawer);
    }

    // Voir le solde actuel
    public BigDecimal getCurrentBalance(Long agentId) {
        CashDrawer cashDrawer = cashDrawerRepository.findByAgentId(agentId)
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));
        return cashDrawer.getBalance();
    }

    // Voir les caisses d'une agence
    public List<CashDrawer> getAgencyCashDrawers(Long agencyId) {
        return cashDrawerRepository.findByAgencyId(agencyId);
    }
}