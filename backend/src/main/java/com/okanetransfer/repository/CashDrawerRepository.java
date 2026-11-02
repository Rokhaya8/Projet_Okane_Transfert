package com.okanetransfer.repository;

import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.enums.CashDrawerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashDrawerRepository extends JpaRepository<CashDrawer, Long> {

    Optional<CashDrawer> findByAgentId(Long agentId);

    List<CashDrawer> findByAgencyId(Long agencyId);

    List<CashDrawer> findByAgencyIdAndStatus(Long agencyId, CashDrawerStatus status);

    List<CashDrawer> findByAgencyIdOrderByOpenedAtDesc(Long agencyId);

    Optional<CashDrawer> findByAgentIdAndStatus(Long agentId, CashDrawerStatus status);

    long countByAgencyIdAndStatus(Long agencyId, CashDrawerStatus status);
}
