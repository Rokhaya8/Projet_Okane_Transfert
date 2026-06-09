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

    // Recherche par code ou téléphone bénéficiaire
    @Query("SELECT t FROM Transfer t WHERE t.referenceCode = :code OR t.beneficiary.phone = :phone")
    List<Transfer> findByReferenceCodeOrBeneficiaryPhone(@Param("code") String code,
                                                          @Param("phone") String phone);

    // Historique paginé avec filtres dynamiques
    @Query("SELECT t FROM Transfer t WHERE " +
           "(t.agent.id = :agentId OR t.payingAgent.id = :agentId) " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "AND (:search IS NULL OR t.referenceCode LIKE %:search% " +
           "     OR t.beneficiary.fullName LIKE %:search% " +
           "     OR t.beneficiary.phone LIKE %:search%) " +
           "AND (:corridor IS NULL OR CONCAT(t.corridor.sourceCurrency.code, '->', t.corridor.destinationCurrency.code) = :corridor)")
    Page<Transfer> findHistoryByAgent(@Param("agentId") Long agentId,
                                       @Param("status") Transfer.TransferStatus status,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       @Param("search") String search,
                                       @Param("corridor") String corridor,
                                       Pageable pageable);
}