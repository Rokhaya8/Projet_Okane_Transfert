package com.okanetransfer.repository;

import com.okanetransfer.entity.AgentCashSession;
import com.okanetransfer.entity.CashSessionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentCashSessionRepository extends JpaRepository<AgentCashSession, Long> {

    Optional<AgentCashSession> findByAgentIdAndStatus(Long agentId, CashSessionStatus status);

    boolean existsByAgentIdAndStatus(Long agentId, CashSessionStatus status);

    List<AgentCashSession> findByAgentIdOrderByOpenedAtDesc(Long agentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AgentCashSession> findWithLockByAgentIdAndStatus(Long agentId, CashSessionStatus status);
}
