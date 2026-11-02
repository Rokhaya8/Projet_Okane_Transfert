package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("CLIENT")
public class Client extends User {

    @Column(length = 20)
    private String cin;

    private LocalDate dateNaissance;

    private String adresse;

    public Client() {
        this.setRole(Role.ROLE_CLIENT);
    }
}