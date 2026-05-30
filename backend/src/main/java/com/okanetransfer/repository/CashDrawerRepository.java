package com.okanetransfer.repository;

import com.okanetransfer.entity.CashDrawer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CashDrawerRepository extends JpaRepository<CashDrawer, Long> {

    // Trouver la caisse d'un agent
    Optional<CashDrawer> findByAgentId(Long agentId);

    // Trouver les caisses d'une agence
    List<CashDrawer> findByAgencyId(Long agencyId);

    // Trouver les caisses ouvertes d'une agence
    List<CashDrawer> findByAgencyIdAndStatus(Long agencyId, CashDrawer.CashDrawerStatus status);
}