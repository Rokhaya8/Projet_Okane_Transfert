package com.okanetransfer.service;

import com.okanetransfer.dto.request.CurrencyRequest;
import com.okanetransfer.dto.response.CurrencyResponse;
import com.okanetransfer.entity.Currency;
import com.okanetransfer.exception.CurrencyAlreadyExistsException;
import com.okanetransfer.exception.CurrencyNotFoundException;
import com.okanetransfer.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<CurrencyResponse> getAllCurrencies() {
        return currencyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CurrencyResponse getCurrency(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));
        return toResponse(currency);
    }

    public CurrencyResponse createCurrency(CurrencyRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("Currency code is required");
        }
        if (currencyRepository.existsByCode(request.getCode())) {
            throw new CurrencyAlreadyExistsException("Currency with this code already exists");
        }
        Currency currency = toEntity(request);
        return toResponse(currencyRepository.save(currency));
    }

    public CurrencyResponse updateCurrency(Long id, CurrencyRequest request) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (!request.getCode().equals(currency.getCode())
                    && currencyRepository.existsByCode(request.getCode())) {
                throw new CurrencyAlreadyExistsException("Currency with this code already exists");
            }
            currency.setCode(request.getCode());
        }
        if (request.getName() != null) currency.setName(request.getName());
        if (request.getSymbol() != null) currency.setSymbol(request.getSymbol());
        currency.setActive(request.isActive());

        return toResponse(currencyRepository.save(currency));
    }

    public void deleteCurrency(Long id) {
        if (!currencyRepository.existsById(id)) {
            throw new CurrencyNotFoundException("Currency not found");
        }
        currencyRepository.deleteById(id);
    }

    // ---- Mappers ----
    private CurrencyResponse toResponse(Currency currency) {
        CurrencyResponse response = new CurrencyResponse();
        response.setId(currency.getId());
        response.setCode(currency.getCode());
        response.setName(currency.getName());
        response.setSymbol(currency.getSymbol());
        response.setActive(currency.isActive());
        return response;
    }

    private Currency toEntity(CurrencyRequest request) {
        Currency currency = new Currency();
        currency.setCode(request.getCode());
        currency.setName(request.getName());
        currency.setSymbol(request.getSymbol());
        currency.setActive(request.isActive());
        return currency;
    }
}