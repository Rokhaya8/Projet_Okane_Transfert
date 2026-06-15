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

    // Chercher par code de retrait
    Optional<Transfer> findByReferenceCode(String referenceCode);

    // Chercher par téléphone du bénéficiaire
    List<Transfer> findByBeneficiaryPhone(String phone);

    // Chercher les transferts d'un agent
    List<Transfer> findByAgentId(Long agentId);

    // Chercher les transferts d'un client
    List<Transfer> findByClientId(Long clientId);

    // Chercher par statut
    List<Transfer> findByStatus(Transfer.TransferStatus status);

    //Suivi de transfert on charge le transfert et ses relations associées
    @Query("SELECT t FROM Transfer t " +
            "LEFT JOIN FETCH t.client " +
            "LEFT JOIN FETCH t.beneficiary " +
            "LEFT JOIN FETCH t.agency " +
            "LEFT JOIN FETCH t.corridor " +
            "WHERE t.referenceCode = :referenceCode")
    Optional<Transfer> findByReferenceCodeWithAllRelations(@Param("referenceCode") String referenceCode);

    //Historique des transferts d'un client spécifique
    @Query("SELECT t FROM Transfer t " +
            "LEFT JOIN FETCH t.client " +
            "LEFT JOIN FETCH t.beneficiary " +
            "LEFT JOIN FETCH t.corridor " +
            "WHERE t.client.id = :clientId " +
            "ORDER BY t.createdAt DESC")
    List<Transfer> findByClientIdWithRelations(@Param("clientId") Long clientId);


    //Dashboard KPI la somme des montants envoyés pour les transferts payés
    @Query("SELECT SUM(t.amountSent) FROM Transfer t WHERE t.client.id = :clientId AND t.status = :status")
    Double sumTotalSentByClient(@Param("clientId") Long clientId, @Param("status") com.okanetransfer.entity.Transfer.TransferStatus status);

}