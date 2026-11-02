package com.okanetransfer.repository;

import com.okanetransfer.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {
    List<Agency> findByName(String name);
    List<Agency> findByActiveTrue();
    Optional<Agency> findByManagerId(Long managerId);
}