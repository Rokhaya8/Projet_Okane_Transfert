package com.okanetransfer.repository;

import com.okanetransfer.entity.TransferPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferPaymentRepository extends JpaRepository<TransferPayment, Long> {

    Optional<TransferPayment> findByTransferReferenceCode(String referenceCode);

    List<TransferPayment> findByAgentIdOrderByPaidAtDesc(Long agentId);
}
