package com.okanetransfer.service;

import com.okanetransfer.entity.*;
import com.okanetransfer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FeeService {

    @Autowired
    private TransferCorridorRepository corridorRepository;

    @Autowired
    private FeeTierRepository feeTierRepository;

    // Calculer les frais selon le montant et le corridor
    public BigDecimal calculateFees(Long corridorId, BigDecimal amount) {

        // 1. Trouver la tranche tarifaire correspondante
        FeeTier feeTier = feeTierRepository
                .findByFeeGridCorridorIdAndAmountBetween(corridorId, amount)
                .orElseThrow(() -> new RuntimeException("Aucune tranche tarifaire trouvée"));

        // 2. Calculer les frais
        // Frais = frais fixes + (montant × pourcentage)
        BigDecimal percentageFee = amount.multiply(feeTier.getPercentageFee())
                .divide(BigDecimal.valueOf(100));

        return feeTier.getFixedFee().add(percentageFee);
    }

    // Trouver le corridor selon pays source et destination
    public TransferCorridor findCorridor(String sourceCountry, String destinationCountry) {
        return corridorRepository
                .findBySourceCountryAndDestinationCountry(sourceCountry, destinationCountry)
                .orElseThrow(() -> new RuntimeException("Corridor introuvable"));
    }
}