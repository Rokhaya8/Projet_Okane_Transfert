package com.okanetransfer.controller;

import com.okanetransfer.dto.request.CurrencyRequest;
import com.okanetransfer.dto.response.CurrencyResponse;
import com.okanetransfer.entity.Currency;
import com.okanetransfer.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CurrencyResponse createCurrency(@RequestBody CurrencyRequest request) {
        return currencyService.createCurrency(request);
    }

    @PutMapping("/{id}")
    public CurrencyResponse updateCurrency(@PathVariable("id") Long id, @RequestBody CurrencyRequest request) {
        return currencyService.updateCurrency(id, request);
    }

    @GetMapping
    public List<CurrencyResponse> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }

    @GetMapping("/{id}")
    public CurrencyResponse getCurrency(@PathVariable("id") Long id) {
        return currencyService.getCurrency(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrency(@PathVariable("id") Long id) {
        currencyService.deleteCurrency(id);
    }
}