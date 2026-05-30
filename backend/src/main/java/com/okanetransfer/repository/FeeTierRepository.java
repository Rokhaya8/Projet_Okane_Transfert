package com.okanetransfer.repository;

import com.okanetransfer.entity.FeeTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface FeeTierRepository extends JpaRepository<FeeTier, Long> {

    @Query("SELECT ft FROM FeeTier ft WHERE ft.feeGrid.corridor.id = :corridorId " +
            "AND ft.minAmount <= :amount AND ft.maxAmount >= :amount")
    Optional<FeeTier> findByFeeGridCorridorIdAndAmountBetween(
            Long corridorId, BigDecimal amount);
}