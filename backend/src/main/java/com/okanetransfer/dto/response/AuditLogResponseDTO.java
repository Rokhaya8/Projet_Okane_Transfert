package com.okanetransfer.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogResponseDTO {
    private Long id;
    private String username;
    private String action;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;
}