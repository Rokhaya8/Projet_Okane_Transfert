package com.okanetransfer.service;

import com.okanetransfer.entity.ExchangeRate;
import com.okanetransfer.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    // Convertir un montant d'une devise à une autre
    public BigDecimal convert(Long currencyId, BigDecimal amount) {

        // 1. Trouver le taux de change actuel
        ExchangeRate exchangeRate = exchangeRateRepository
                .findLatestByCurrencyId(currencyId)
                .orElseThrow(() -> new RuntimeException("Taux de change introuvable"));

        // 2. Calculer le montant converti
        return amount.multiply(exchangeRate.getRate());
    }

    // Obtenir le taux actuel
    public BigDecimal getCurrentRate(Long currencyId) {
        ExchangeRate exchangeRate = exchangeRateRepository
                .findLatestByCurrencyId(currencyId)
                .orElseThrow(() -> new RuntimeException("Taux de change introuvable"));
        return exchangeRate.getRate();
    }
}