package com.okanetransfer.repository;

import com.okanetransfer.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    boolean existsByCode(String code);
}