package com.okanetransfer.controller;

import com.okanetransfer.dto.*;
import com.okanetransfer.dto.request.AddAgentRequest;
import com.okanetransfer.dto.request.ChangePasswordRequest;
import com.okanetransfer.dto.request.RejectValidationRequest;
import com.okanetransfer.dto.request.UpdateAgentRequest;
import com.okanetransfer.entity.Transfer;
import com.okanetransfer.enums.ReportPeriod;
import com.okanetransfer.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/agents")
    public List<UserDTO> getAgents() {
        return managerService.getAgents();
    }

    @PostMapping("/agents")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO addAgent(@Valid @RequestBody AddAgentRequest request) {
        return managerService.addAgent(request);
    }

    @GetMapping("/agents/{id}")
    public AgentDetailDTO getAgentDetail(@PathVariable("id") Long id) {
        return managerService.getAgentDetail(id);
    }

    @PutMapping("/agents/{id}")
    public UserDTO updateAgent(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateAgentRequest request) {
        return managerService.updateAgent(id, request);
    }

    @PatchMapping("/agents/{id}/activate")
    public UserDTO activateAgent(@PathVariable("id") Long id) {
        return managerService.activateAgent(id);
    }

    @PatchMapping("/agents/{id}/suspend")
    public UserDTO suspendAgent(@PathVariable("id") Long id) {
        return managerService.suspendAgent(id);
    }

    @GetMapping("/validations")
    public List<SensitiveOperationDTO> getPendingValidations() {
        return managerService.getPendingValidations();
    }

    @PostMapping("/validations/{id}/approve")
    public SensitiveOperationDTO approveTransfer(@PathVariable("id") Long id) {
        return managerService.approveTransfer(id);
    }

    @PostMapping("/validations/{id}/reject")
    public SensitiveOperationDTO rejectTransfer(
            @PathVariable("id") Long id,
            @Valid @RequestBody RejectValidationRequest request) {
        return managerService.rejectTransfer(id, request.getReason());
    }

    @GetMapping("/reports")
    public AgencyReportDTO getAgencyReport(@RequestParam(value = "period",defaultValue = "DAY") ReportPeriod period) {
        return managerService.getAgencyReport(period);
    }

    @GetMapping("/transactions")
    public List<TransferDTO> getTransactions(
            @RequestParam(value = "status",required = false) Transfer.TransferStatus status,
            @RequestParam(value = "from",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return managerService.getTransactions(status, from, to);
    }

    @GetMapping("/operations")
    public List<OperationDTO> getOperations(
            @RequestParam(value = "status",required = false) Transfer.TransferStatus status,
            @RequestParam(value = "from",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return managerService.getOperations(status, from, to);
    }

    @GetMapping("/operations/{id}")
    public OperationDetailDTO getOperationDetail(@PathVariable("id") Long id) {
        return managerService.getOperationDetail(id);
    }

    @GetMapping("/performance")
    public AgencyPerformanceDTO getPerformance() {
        return managerService.getPerformance();
    }

    @GetMapping("/performance/agents")
    public List<AgentPerformanceDTO> getAgentPerformance(
            @RequestParam(value = "from",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return managerService.getAgentPerformance(from, to);
    }

    @GetMapping("/profile")
    public ManagerProfileDTO getProfile() {
        return managerService.getProfile();
    }

    @PutMapping("/profile")
    public ManagerProfileDTO updateProfile(@Valid @RequestBody UpdateAgentRequest request) {
        return managerService.updateProfile(request);
    }

    @PutMapping("/profile/password")
    public Map<String, String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        managerService.changePassword(request);
        return Map.of("message", "Mot de passe modifie");
    }

    @GetMapping("/reports/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(value = "from",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-agence.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(managerService.exportReportCsv(from, to));
    }

    @GetMapping("/reports/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(value = "from",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport-agence.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(managerService.exportReportPdf(from, to));
    }

    @GetMapping("/cash-drawers")
    public List<CashDrawerDTO> getCashDrawers() {
        return managerService.getCashDrawers();
    }
}
