package com.okanetransfer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entityType;

    private Long entityId;

    private String details;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}