package com.okanetransfer.repository;

import com.okanetransfer.entity.SensitiveOperation;
import com.okanetransfer.enums.SensitiveOperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensitiveOperationRepository extends JpaRepository<SensitiveOperation, Long> {

    List<SensitiveOperation> findByAgencyIdAndStatusOrderByCreatedAtDesc(
            Long agencyId, SensitiveOperationStatus status);

    long countByAgencyIdAndStatus(Long agencyId, SensitiveOperationStatus status);
}
