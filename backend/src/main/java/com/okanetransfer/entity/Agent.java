package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ROLE_AGENT")
public class Agent extends User {

    @Column(name = "matricule")
    private String matricule;

    @Column(name = "commission_rate")
    private Double commissionRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    public Agent() {
        setRole(Role.ROLE_AGENT);
    }
}
