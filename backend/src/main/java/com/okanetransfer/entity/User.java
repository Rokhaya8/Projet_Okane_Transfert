package com.okanetransfer.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
// Stratégie pour stocker tous les rôles (Agent, Admin, etc.) dans la même table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Colonne technique qui permet à Hibernate de distinguer le type d'entité (ex: 'AGENT')
@DiscriminatorColumn(name = "user_type")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    // Identifiant unique généré automatiquement par la base
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom complet de l'utilisateur
    @Column(nullable = false)
    private String fullName;

    // Adresse email utilisée pour la connexion (doit être unique)
    @Column(nullable = false, unique = true)
    private String email;

    // Mot de passe crypté de l'utilisateur
    @Column(nullable = false)
    private String password;


    // État du compte : true si actif, false si suspendu
    @Column(nullable = false)
    private boolean active = true;

    // Date de création du compte dans le système
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Date de la dernière connexion réussie
    private LocalDateTime lastLogin;

    // Rôle métier de l'utilisateur pour la gestion des droits
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Liste des rôles possibles dans l'application
    public enum Role {
        ROLE_ADMIN,
        ROLE_MANAGER,
        ROLE_AGENT,
        ROLE_CLIENT
    }



}