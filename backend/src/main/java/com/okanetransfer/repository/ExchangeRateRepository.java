package com.okanetransfer.repository;

import com.okanetransfer.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    // Trouver le taux le plus récent pour une devise
    @Query("SELECT e FROM ExchangeRate e WHERE e.currency.id = :currencyId " +
            "ORDER BY e.effectiveDate DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByCurrencyId(Long currencyId);
}