package com.okanetransfer.repository;

import com.okanetransfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Chercher les transferts d'un expéditeur
    List<Transfer> findBySenderId(Long senderId);

    // Chercher par statut
    List<Transfer> findByStatus(Transfer.TransferStatus status);
}