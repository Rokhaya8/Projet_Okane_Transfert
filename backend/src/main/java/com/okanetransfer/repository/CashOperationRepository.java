package com.okanetransfer.repository;

import com.okanetransfer.entity.CashOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CashOperationRepository extends JpaRepository<CashOperation, Long> {

    List<CashOperation> findByCashDrawerIdAndOperationDateBetweenOrderByOperationDateDesc(
            Long cashDrawerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(CASE WHEN o.type = 'ENCAISSEMENT' THEN o.amount ELSE -o.amount END), 0) " +
           "FROM CashOperation o WHERE o.cashDrawer.id = :drawerId " +
           "AND o.operationDate BETWEEN :start AND :end AND o.type <> 'CLOTURE'")
    BigDecimal sumNetOperationsForDay(@Param("drawerId") Long drawerId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    boolean existsByCashDrawerIdAndTypeAndOperationDateBetween(
            Long cashDrawerId, CashOperation.OperationType type,
            LocalDateTime start, LocalDateTime end);
}
