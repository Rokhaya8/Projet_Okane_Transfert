package com.okanetransfer.repository;

import com.okanetransfer.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByEmail(String email);

    Optional<Agent> findByMatricule(String matricule);

    List<Agent> findByAgencyId(Long agencyId);

    long countByAgencyIdAndActiveTrue(Long agencyId);
}
