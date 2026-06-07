package com.okanetransfer.repository;

import com.okanetransfer.entity.TransferCorridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferCorridorRepository extends JpaRepository<TransferCorridor, Long> {

    List<TransferCorridor> findByActiveTrue();

    boolean existsBySourceCurrencyIdAndDestinationCurrencyId(Long sourceCurrencyId, Long destinationCurrencyId);
}