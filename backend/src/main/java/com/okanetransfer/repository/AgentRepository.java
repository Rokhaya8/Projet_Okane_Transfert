package com.okanetransfer.repository;

import com.okanetransfer.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    List<Agent> findByAgencyId(Long agencyId);

    long countByAgencyIdAndActiveTrue(Long agencyId);
}
