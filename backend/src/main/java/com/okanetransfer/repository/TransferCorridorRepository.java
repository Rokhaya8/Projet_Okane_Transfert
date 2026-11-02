package com.okanetransfer.repository;

import com.okanetransfer.entity.TransferCorridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferCorridorRepository extends JpaRepository<TransferCorridor, Long> {
    List<TransferCorridor> findByActiveTrue();
    boolean existsBySourceCurrencyIdAndDestinationCurrencyId(Long sourceCurrencyId, Long destinationCurrencyId);
    List<TransferCorridor> findBySourceCountryAndActiveTrue(String sourceCountry);
    Optional<TransferCorridor> findBySourceCountryAndDestinationCountry(String sourceCountry, String destinationCountry);
}