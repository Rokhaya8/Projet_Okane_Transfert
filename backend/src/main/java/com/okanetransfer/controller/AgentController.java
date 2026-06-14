package com.okanetransfer.controller;

import com.okanetransfer.dto.AgentProfileDTO;
import com.okanetransfer.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.okanetransfer.dto.ReceptionCountryDTO;
import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<AgentProfileDTO> getAgentProfile(@PathVariable Long id) {
        AgentProfileDTO dto = agentService.getAgentProfileDto(id);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/reception-countries")
    public ResponseEntity<List<ReceptionCountryDTO>> getReceptionCountries(
            @RequestParam String sourceCountry) {
        return ResponseEntity.ok(agentService.getReceptionCountries(sourceCountry));
    }

}