package com.okanetransfer.repository;

import com.okanetransfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("""
            SELECT t FROM Transfer t
            WHERE (t.sourceAgency.id = :agencyId OR t.destinationAgency.id = :agencyId)
              AND (:status IS NULL OR t.status = :status)
              AND t.createdAt >= :from
              AND t.createdAt <= :to
            ORDER BY t.createdAt DESC
            """)
    List<Transfer> findAgencyTransactions(
            @Param("agencyId") Long agencyId,
            @Param("status") Transfer.TransferStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT t FROM Transfer t
            WHERE (t.sourceAgency.id = :agencyId OR t.destinationAgency.id = :agencyId)
              AND t.createdAt >= :from
              AND t.createdAt <= :to
            """)
    List<Transfer> findAgencyTransfersInPeriod(
            @Param("agencyId") Long agencyId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT t FROM Transfer t
            WHERE t.id = :transferId
              AND (t.sourceAgency.id = :agencyId OR t.destinationAgency.id = :agencyId)
            """)
    Optional<Transfer> findAgencyTransferById(
            @Param("agencyId") Long agencyId,
            @Param("transferId") Long transferId);

    List<Transfer> findByAgentIdOrderByCreatedAtDesc(Long agentId);

    long countBySourceAgencyIdAndStatusAndCreatedAtBetween(
            Long agencyId, Transfer.TransferStatus status, LocalDateTime from, LocalDateTime to);
}
