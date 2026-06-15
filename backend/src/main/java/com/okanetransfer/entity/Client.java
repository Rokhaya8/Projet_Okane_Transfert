package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true) // Important avec Lombok pour inclure les champs de User dans le equals/hashCode
@Entity
@DiscriminatorValue("CLIENT") // Valeur qui sera inscrite dans la colonne 'user_type' pour un client
public class Client extends User {

    // Informations requises spécifiquement pour un client selon le CDC
    @Column(length = 20)
    private String cin; // Numéro de carte d'identité ou passeport

    private LocalDate dateNaissance;

    private String adresse;

    // Le constructeur par défaut force le rôle à ROLE_CLIENT
    public Client() {
        this.setRole(Role.ROLE_CLIENT);
    }
}

