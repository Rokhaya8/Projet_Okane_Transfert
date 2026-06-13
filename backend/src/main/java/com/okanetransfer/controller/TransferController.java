package com.okanetransfer.controller;

import com.okanetransfer.dto.response.TransferResponse;
import com.okanetransfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    public ResponseEntity<List<TransferResponse>> getAll() {
        return ResponseEntity.ok(transferService.getAll());
    }
}