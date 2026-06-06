package com.okanetransfer.controller;

import com.okanetransfer.dto.AgentProfileDTO;
import com.okanetransfer.entity.User;
import com.okanetransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.okanetransfer.dto.ReceptionCountryDTO;
import com.okanetransfer.repository.TransferCorridorRepository;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agent")
public class AgentController {


    @Autowired
    private TransferCorridorRepository corridorRepository;

    // Liste des pays de réception disponibles (depuis les corridors actifs) i.e.
    // Liste des pays de réception disponibles depuis le pays de l'agent
    @GetMapping("/reception-countries/{agentId}")
    public ResponseEntity<List<ReceptionCountryDTO>> getReceptionCountries(@PathVariable Long agentId) {

        // 1. Récupérer l'agent
        User agent = userRepository.findById(agentId).orElse(null);
        if (agent == null || agent.getAgency() == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Le pays source = le pays de l'agence de l'agent
        String sourceCountry = agent.getAgency().getCountry();

        // 3. Récupérer les corridors actifs depuis ce pays
        List<ReceptionCountryDTO> countries = corridorRepository
                .findBySourceCountryAndActiveTrue(sourceCountry)
                .stream()
                .map(c -> new ReceptionCountryDTO(
                        c.getDestinationCountry(),
                        c.getDestinationCurrency() != null ? c.getDestinationCurrency().getCode() : null,
                        c.getDestinationCurrency() != null ? c.getDestinationCurrency().getName() : null,
                        c.getSourceCurrency() != null ? c.getSourceCurrency().getCode() : null   // ← devise d'envoi
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(countries);
    }
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile/{agentId}")
    public ResponseEntity<AgentProfileDTO> getProfile(@PathVariable Long agentId) {
        User agent = userRepository.findById(agentId).orElse(null);

        if (agent == null) {
            return ResponseEntity.notFound().build();
        }

        // On construit le DTO avec SEULEMENT les infos utiles
        AgentProfileDTO dto = new AgentProfileDTO(
                agent.getId(),
                agent.getFullName(),
                agent.getAgency() != null ? agent.getAgency().getName() : null,
                agent.getAgency() != null ? agent.getAgency().getCountry() : null
        );

        return ResponseEntity.ok(dto);
    }
}