package com.okanetransfer.repository;

import com.okanetransfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReferenceCode(String referenceCode);

    List<Transfer> findByBeneficiaryPhone(String phone);
    List<Transfer> findByBeneficiaryPhoneAndStatus(String phone, Transfer.TransferStatus status);
    List<Transfer> findByAgentId(Long agentId);
    List<Transfer> findByAgentIdOrderByCreatedAtDesc(Long agentId);
    List<Transfer> findBySenderId(Long senderId);
    List<Transfer> findByStatus(Transfer.TransferStatus status);

    @Query("""
            SELECT t FROM Transfer t
            LEFT JOIN FETCH t.client
            LEFT JOIN FETCH t.beneficiary
            LEFT JOIN FETCH t.agency
            LEFT JOIN FETCH t.corridor
            WHERE t.referenceCode = :referenceCode
            """)
    Optional<Transfer> findByReferenceCodeWithAllRelations(@Param("referenceCode") String referenceCode);

    @Query("""
            SELECT t FROM Transfer t
            LEFT JOIN FETCH t.client
            LEFT JOIN FETCH t.beneficiary
            LEFT JOIN FETCH t.corridor
            WHERE t.client.id = :clientId
            ORDER BY t.createdAt DESC
            """)
    List<Transfer> findByClientIdWithRelations(@Param("clientId") Long clientId);

    @Query("SELECT SUM(t.amountSent) FROM Transfer t WHERE t.client.id = :clientId AND t.status = :status")
    Double sumTotalSentByClient(@Param("clientId") Long clientId, @Param("status") Transfer.TransferStatus status);

    @Query("""
            SELECT t FROM Transfer t
            WHERE (t.sourceAgency.id = :agencyId OR t.destinationAgency.id = :agencyId OR t.agency.id = :agencyId OR t.payingAgency.id = :agencyId)
              AND (:status IS NULL OR t.status = :status)
              AND (:agentId IS NULL OR t.agent.id = :agentId)
              AND t.createdAt >= :from
              AND t.createdAt <= :to
            ORDER BY t.createdAt DESC
            """)
    List<Transfer> findAgencyTransactions(
            @Param("agencyId") Long agencyId,
            @Param("status") Transfer.TransferStatus status,
            @Param("agentId") Long agentId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("""
            SELECT t FROM Transfer t
            WHERE (t.sourceAgency.id = :agencyId OR t.destinationAgency.id = :agencyId OR t.agency.id = :agencyId OR t.payingAgency.id = :agencyId)
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
              AND (t.sourceAgency.id = :agencyId OR t.destinationAgency.id = :agencyId OR t.agency.id = :agencyId OR t.payingAgency.id = :agencyId)
            """)
    Optional<Transfer> findAgencyTransferById(
            @Param("agencyId") Long agencyId,
            @Param("transferId") Long transferId);

    long countBySourceAgencyIdAndStatusAndCreatedAtBetween(
            Long agencyId, Transfer.TransferStatus status, LocalDateTime from, LocalDateTime to);
}