package com.okanetransfer.service;

import com.okanetransfer.dto.AgentProfileDTO;
import com.okanetransfer.dto.ReceptionCountryDTO;          // ← AJOUT (adapte si tu as mis le DTO ailleurs)
import com.okanetransfer.entity.Agent;
import com.okanetransfer.entity.User;
import com.okanetransfer.repository.AgentRepository;
import com.okanetransfer.repository.TransferCorridorRepository; // ← AJOUT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;                                       // ← AJOUT
import java.util.stream.Collectors;                          // ← AJOUT

@Service
public class AgentService {

    // En haut de la classe AgentService, comme constante
    private static final java.util.Map<String, String> INDICATIFS = java.util.Map.of(
            "Sénégal", "+221",
            "France", "+33",
            "Maroc", "+212",
            "Mali", "+223",
            "Côte d'Ivoire", "+225"
            // ajoute les pays dont tu as besoin
    );

    @Autowired
    private AgentRepository agentRepository;                 // (voir note plus bas)

    @Autowired
    private TransferCorridorRepository corridorRepository;   // ← AJOUT

    @Transactional
    public AgentProfileDTO getAgentProfileDto(Long id) {
        // 👇 ICI, tout au début
        System.out.println(">>> transaction active = " +
                org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive());
        User user = agentRepository.findById(id).orElse(null);

        if (!(user instanceof Agent)) {
            return null;
        }

        Agent agent = (Agent) user;


        System.out.println(">>> agent class = " + agent.getClass());
        System.out.println(">>> agency = " + agent.getAgency());


        return new AgentProfileDTO(
                agent.getId(),
                agent.getFullName(),
                agent.getAgency() != null ? agent.getAgency().getName() : null,
                agent.getAgency() != null ? agent.getAgency().getCountry() : null
        );
    }

    // ── AJOUT : pays de réception disponibles depuis un pays source ──
    public List<ReceptionCountryDTO> getReceptionCountries(String sourceCountry) {
        return corridorRepository.findBySourceCountryAndActiveTrue(sourceCountry)
                .stream()
                .map(c -> new ReceptionCountryDTO(
                        c.getDestinationCountry(),                            // country
                        c.getDestinationCurrency().getCode(),                 // currencyCode (réception)
                        c.getDestinationCurrency().getName(),                 // currencyName
                        c.getSourceCurrency().getCode(),                      // sourceCurrencyCode (envoi)
                        INDICATIFS.getOrDefault(c.getDestinationCountry(), "") // ← phoneCode (l'indicatif)
                ))
                .collect(Collectors.toList());
    }
}