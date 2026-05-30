package com.okanetransfer.repository;

import com.okanetransfer.entity.TransferCorridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransferCorridorRepository extends JpaRepository<TransferCorridor, Long> {

    Optional<TransferCorridor> findBySourceCountryAndDestinationCountry(
            String sourceCountry, String destinationCountry);
}