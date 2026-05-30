package com.okanetransfer.repository;

import com.okanetransfer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Trouver par email (pour le login)
    Optional<User> findByEmail(String email);

    // Trouver par rôle
    List<User> findByRole(User.Role role);

    // Vérifier si email existe déjà
    boolean existsByEmail(String email);
}