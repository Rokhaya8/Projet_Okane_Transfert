package com.okanetransfer.service;

import com.okanetransfer.annotation.Auditable;
import com.okanetransfer.dto.response.AuditLogResponseDTO;
import com.okanetransfer.entity.AuditLog;
import com.okanetransfer.entity.User;
import com.okanetransfer.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {
     @Autowired
    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String entityType,
                    Long entityId, String details, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
    public List<AuditLogResponseDTO> findAll() {
        List<AuditLog> logs = auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));

        return logs.stream().map(log -> {
            AuditLogResponseDTO dto = new AuditLogResponseDTO();
            dto.setId(log.getId());

            dto.setUsername(log.getUser() != null ? log.getUser().getFullName() : "___");
            dto.setAction(log.getAction());
            dto.setDetails(log.getDetails());
            dto.setIpAddress(log.getIpAddress());
            dto.setTimestamp(log.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}