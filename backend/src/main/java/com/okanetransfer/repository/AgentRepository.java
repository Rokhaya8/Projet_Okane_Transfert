package com.okanetransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.okanetransfer.entity.Agent;
import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    // Comme Agent hérite de User, tu peux même chercher un agent par email !
    Optional<Agent> findByEmail(String email);

    // Ou chercher par matricule (spécifique à l'agent)
    Optional<Agent> findByMatricule(String matricule);
}