package com.okanetransfer.service;

import com.okanetransfer.entity.AuditLog;
import com.okanetransfer.entity.User;
import com.okanetransfer.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(Long userId, String action, String entityType, Long entityId, String details, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setIpAddress(ipAddress != null ? ipAddress : "UNKNOWN");
        auditLogRepository.save(log);
    }
}
