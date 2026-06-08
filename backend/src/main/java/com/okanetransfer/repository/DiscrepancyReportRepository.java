package com.okanetransfer.repository;

import com.okanetransfer.entity.DiscrepancyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscrepancyReportRepository extends JpaRepository<DiscrepancyReport, Long> {
}
