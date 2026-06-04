package com.okanetransfer.repository;

import com.okanetransfer.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    Optional<Transfer> findByReferenceCode(String referenceCode);

    Optional<Transfer> findByBeneficiaryPhoneAndStatus(String phone, Transfer.TransferStatus status);

    List<Transfer> findByBeneficiaryPhone(String phone);

    List<Transfer> findByAgentId(Long agentId);

    List<Transfer> findByClientId(Long clientId);

    List<Transfer> findByStatus(Transfer.TransferStatus status);

    @Query("SELECT t FROM Transfer t WHERE t.agent.id = :agentId " +
           "AND (t.createdAt >= :startDate OR :startDate IS NULL) " +
           "AND (t.createdAt <= :endDate OR :endDate IS NULL)")
    Page<Transfer> findAgentHistoryNoStatus(@Param("agentId") Long agentId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);

    @Query("SELECT t FROM Transfer t WHERE t.agent.id = :agentId " +
           "AND t.status = :status " +
           "AND (t.createdAt >= :startDate OR :startDate IS NULL) " +
           "AND (t.createdAt <= :endDate OR :endDate IS NULL)")
    Page<Transfer> findAgentHistoryWithStatus(@Param("agentId") Long agentId,
                                              @Param("status") Transfer.TransferStatus status,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              Pageable pageable);

    List<Transfer> findByAgentIdAndCreatedAtBetween(Long agentId, LocalDateTime start, LocalDateTime end);
}