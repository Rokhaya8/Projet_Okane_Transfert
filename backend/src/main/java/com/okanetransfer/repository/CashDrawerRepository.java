package com.okanetransfer.repository;

import com.okanetransfer.entity.CashDrawer;
import com.okanetransfer.enums.CashDrawerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashDrawerRepository extends JpaRepository<CashDrawer, Long> {

    List<CashDrawer> findByAgencyIdOrderByOpenedAtDesc(Long agencyId);

    Optional<CashDrawer> findByAgentIdAndStatus(Long agentId, CashDrawerStatus status);

    long countByAgencyIdAndStatus(Long agencyId, CashDrawerStatus status);
}
