package com.okanetransfer.controller;

import com.okanetransfer.dto.response.AuditLogResponseDTO;
import com.okanetransfer.entity.AuditLog;
import com.okanetransfer.repository.AuditLogRepository;
import com.okanetransfer.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
     @Autowired
    private final AuditLogService auditLogService;



    @GetMapping
    public ResponseEntity<List<AuditLogResponseDTO>> getAll() {

        List<AuditLogResponseDTO> logs = auditLogService.findAll();
        return ResponseEntity.ok(logs);
    }
}