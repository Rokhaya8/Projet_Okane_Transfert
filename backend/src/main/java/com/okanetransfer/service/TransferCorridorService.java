package com.okanetransfer.service;

import com.okanetransfer.dto.request.TransferCorridorRequest;
import com.okanetransfer.dto.response.TransferCorridorResponse;
import com.okanetransfer.entity.Currency;
import com.okanetransfer.entity.TransferCorridor;
import com.okanetransfer.repository.CurrencyRepository;
import com.okanetransfer.repository.TransferCorridorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferCorridorService {

    @Autowired
    private TransferCorridorRepository corridorRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    // ── Lister tous les corridors ──────────────────────────────────────────
    public List<TransferCorridorResponse> getAllCorridors() {
        return corridorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Lister uniquement les corridors actifs ─────────────────────────────
    public List<TransferCorridorResponse> getActiveCorridors() {
        return corridorRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Récupérer un corridor par ID ───────────────────────────────────────
    public TransferCorridorResponse getCorridor(Long id) {
        TransferCorridor corridor = findOrThrow(id);
        return toResponse(corridor);
    }

    // ── Créer un corridor ──────────────────────────────────────────────────
    public TransferCorridorResponse createCorridor(TransferCorridorRequest request) {
        // Vérifier doublon
        if (corridorRepository.existsBySourceCurrencyIdAndDestinationCurrencyId(
                request.getSourceCurrencyId(), request.getDestinationCurrencyId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un corridor existe déjà pour cette paire de devises."
            );
        }

        Currency sourceCurrency = findCurrencyOrThrow(request.getSourceCurrencyId());
        Currency destinationCurrency = findCurrencyOrThrow(request.getDestinationCurrencyId());

        TransferCorridor corridor = new TransferCorridor();
        corridor.setSourceCountry(request.getSourceCountry());
        corridor.setDestinationCountry(request.getDestinationCountry());
        corridor.setSourceCurrency(sourceCurrency);
        corridor.setDestinationCurrency(destinationCurrency);
        corridor.setActive(request.isActive());

        return toResponse(corridorRepository.save(corridor));
    }

    // ── Mettre à jour un corridor ──────────────────────────────────────────
    public TransferCorridorResponse updateCorridor(Long id, TransferCorridorRequest request) {
        TransferCorridor corridor = findOrThrow(id);

        Currency sourceCurrency = findCurrencyOrThrow(request.getSourceCurrencyId());
        Currency destinationCurrency = findCurrencyOrThrow(request.getDestinationCurrencyId());

        corridor.setSourceCountry(request.getSourceCountry());
        corridor.setDestinationCountry(request.getDestinationCountry());
        corridor.setSourceCurrency(sourceCurrency);
        corridor.setDestinationCurrency(destinationCurrency);
        corridor.setActive(request.isActive());

        return toResponse(corridorRepository.save(corridor));
    }

    // ── Activer / Désactiver (toggle) ──────────────────────────────────────
    public TransferCorridorResponse toggleCorridor(Long id) {
        TransferCorridor corridor = findOrThrow(id);
        corridor.setActive(!corridor.isActive());
        return toResponse(corridorRepository.save(corridor));
    }

    // ── Supprimer un corridor ──────────────────────────────────────────────
    public void deleteCorridor(Long id) {
        findOrThrow(id);
        corridorRepository.deleteById(id);
    }

    // ── Helpers privés ─────────────────────────────────────────────────────
    private TransferCorridor findOrThrow(Long id) {
        return corridorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Corridor introuvable avec l'id : " + id
                ));
    }

    private Currency findCurrencyOrThrow(Long currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Devise introuvable avec l'id : " + currencyId
                ));
    }

    private TransferCorridorResponse toResponse(TransferCorridor corridor) {
        TransferCorridorResponse response = new TransferCorridorResponse();
        response.setId(corridor.getId());
        response.setSourceCountry(corridor.getSourceCountry());
        response.setDestinationCountry(corridor.getDestinationCountry());
        response.setActive(corridor.isActive());

        if (corridor.getSourceCurrency() != null) {
            response.setSourceCurrencyId(corridor.getSourceCurrency().getId());
            response.setSourceCurrencyCode(corridor.getSourceCurrency().getCode());
            response.setSourceCurrencySymbol(corridor.getSourceCurrency().getSymbol());
        }

        if (corridor.getDestinationCurrency() != null) {
            response.setDestinationCurrencyId(corridor.getDestinationCurrency().getId());
            response.setDestinationCurrencyCode(corridor.getDestinationCurrency().getCode());
            response.setDestinationCurrencySymbol(corridor.getDestinationCurrency().getSymbol());
        }

        return response;
    }
}