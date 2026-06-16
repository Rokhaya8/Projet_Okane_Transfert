package com.okanetransfer.repository;

import com.okanetransfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReferenceCode(String referenceCode);
    List<Transfer> findByBeneficiaryPhone(String phone);
    List<Transfer> findByBeneficiaryPhoneAndStatus(String phone, Transfer.TransferStatus status);
    List<Transfer> findByAgentId(Long agentId);
    List<Transfer> findBySenderId(Long senderId);
    List<Transfer> findByStatus(Transfer.TransferStatus status);

    @Query("SELECT t FROM Transfer t " +
            "LEFT JOIN FETCH t.client " +
            "LEFT JOIN FETCH t.beneficiary " +
            "LEFT JOIN FETCH t.agency " +
            "LEFT JOIN FETCH t.corridor " +
            "WHERE t.referenceCode = :referenceCode")
    Optional<Transfer> findByReferenceCodeWithAllRelations(@Param("referenceCode") String referenceCode);

    @Query("SELECT t FROM Transfer t " +
            "LEFT JOIN FETCH t.client " +
            "LEFT JOIN FETCH t.beneficiary " +
            "LEFT JOIN FETCH t.corridor " +
            "WHERE t.client.id = :clientId " +
            "ORDER BY t.createdAt DESC")
    List<Transfer> findByClientIdWithRelations(@Param("clientId") Long clientId);

    @Query("SELECT SUM(t.amountSent) FROM Transfer t WHERE t.client.id = :clientId AND t.status = :status")
    Double sumTotalSentByClient(@Param("clientId") Long clientId, @Param("status") com.okanetransfer.entity.Transfer.TransferStatus status);
}