package com.okanetransfer.repository;

import com.okanetransfer.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReferenceCode(String referenceCode);

    Optional<Transfer> findByReferenceCodeAndStatus(String referenceCode, Transfer.TransferStatus status);

    List<Transfer> findByBeneficiaryPhoneAndStatus(String phone, Transfer.TransferStatus status);

    List<Transfer> findByAgentId(Long agentId);

    List<Transfer> findByClientId(Long clientId);

    List<Transfer> findByStatus(Transfer.TransferStatus status);
    
    List<Transfer> findByStatusAndCreatedAtAfter(Transfer.TransferStatus status, LocalDateTime createdAt);

    // Recherche par code ou téléphone bénéficiaire
    @Query("SELECT t FROM Transfer t WHERE t.referenceCode = :code OR t.beneficiary.phone = :phone")
    List<Transfer> findByReferenceCodeOrBeneficiaryPhone(@Param("code") String code,
                                                          @Param("phone") String phone);

    // Historique avec filtres dynamiques - retourne List pour éviter les problèmes de pagination native
    @Query(value = "SELECT * FROM transfers t WHERE " +
           "(t.agent_id = :agentId OR t.paying_agent_id = :agentId) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (CAST(:startDate AS TIMESTAMP) IS NULL OR t.created_at >= CAST(:startDate AS TIMESTAMP)) " +
           "AND (CAST(:endDate AS TIMESTAMP) IS NULL OR t.created_at <= CAST(:endDate AS TIMESTAMP)) " +
           "AND (:search IS NULL OR t.reference_code LIKE CONCAT('%', :search, '%') " +
           "     OR t.beneficiary_name LIKE CONCAT('%', :search, '%') " +
           "     OR t.beneficiary_phone LIKE CONCAT('%', :search, '%')) " +
           "AND (:corridor IS NULL OR CONCAT(t.source_currency, '->', t.destination_currency) = :corridor) " +
           "ORDER BY t.created_at DESC LIMIT :limit OFFSET :offset",
           nativeQuery = true)
    List<Transfer> findHistoryByAgent(@Param("agentId") Long agentId,
                                       @Param("status") String status,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("search") String search,
                                       @Param("corridor") String corridor,
                                       @Param("limit") int limit,
                                       @Param("offset") int offset);
    
    @Query(value = "SELECT COUNT(*) FROM transfers t WHERE " +
           "(t.agent_id = :agentId OR t.paying_agent_id = :agentId) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (CAST(:startDate AS TIMESTAMP) IS NULL OR t.created_at >= CAST(:startDate AS TIMESTAMP)) " +
           "AND (CAST(:endDate AS TIMESTAMP) IS NULL OR t.created_at <= CAST(:endDate AS TIMESTAMP)) " +
           "AND (:search IS NULL OR t.reference_code LIKE CONCAT('%', :search, '%') " +
           "     OR t.beneficiary_name LIKE CONCAT('%', :search, '%') " +
           "     OR t.beneficiary_phone LIKE CONCAT('%', :search, '%')) " +
           "AND (:corridor IS NULL OR CONCAT(t.source_currency, '->', t.destination_currency) = :corridor)",
           nativeQuery = true)
    long countHistoryByAgent(@Param("agentId") Long agentId,
                              @Param("status") String status,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              @Param("search") String search,
                              @Param("corridor") String corridor);
}