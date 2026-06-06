package com.okanetransfer.repository;

import com.okanetransfer.entity.TransferCorridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TransferCorridorRepository extends JpaRepository<TransferCorridor, Long> {

    Optional<TransferCorridor> findBySourceCountryAndDestinationCountry(
            String sourceCountry, String destinationCountry);

    // Corridors actifs depuis un pays source donné
    List<TransferCorridor> findBySourceCountryAndActiveTrue(String sourceCountry);
}