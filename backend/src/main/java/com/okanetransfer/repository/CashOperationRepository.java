package com.okanetransfer.repository;

import com.okanetransfer.entity.CashOperation;
import com.okanetransfer.entity.CashOperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashOperationRepository extends JpaRepository<CashOperation, Long> {

    List<CashOperation> findByCashSessionIdOrderByOperationDateDesc(Long cashSessionId);

    List<CashOperation> findByCashSessionAgentIdOrderByOperationDateDesc(Long agentId);

    List<CashOperation> findByCashSessionAgentIdAndOperationTypeOrderByOperationDateDesc(
            Long agentId,
            CashOperationType operationType
    );
}
