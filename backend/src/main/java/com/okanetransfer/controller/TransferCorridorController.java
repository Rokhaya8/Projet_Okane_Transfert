package com.okanetransfer.controller;

import com.okanetransfer.dto.request.TransferCorridorRequest;
import com.okanetransfer.dto.response.TransferCorridorResponse;
import com.okanetransfer.service.TransferCorridorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/corridors")
public class TransferCorridorController {

    @Autowired
    private TransferCorridorService corridorService;

    // GET /api/admin/corridors
    @GetMapping
    public List<TransferCorridorResponse> getAllCorridors() {
        return corridorService.getAllCorridors();
    }

    // GET /api/admin/corridors/active
    @GetMapping("/active")
    public List<TransferCorridorResponse> getActiveCorridors() {
        return corridorService.getActiveCorridors();
    }

    // GET /api/admin/corridors/{id}
    @GetMapping("/{id}")
    public TransferCorridorResponse getCorridor(@PathVariable("id") Long id) {
        return corridorService.getCorridor(id);
    }

    // POST /api/admin/corridors
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferCorridorResponse createCorridor(@RequestBody TransferCorridorRequest request) {
        return corridorService.createCorridor(request);
    }

    // PUT /api/admin/corridors/{id}
    @PutMapping("/{id}")
    public TransferCorridorResponse updateCorridor(
            @PathVariable("id") Long id,
            @RequestBody TransferCorridorRequest request) {
        return corridorService.updateCorridor(id, request);
    }

    // PATCH /api/admin/corridors/{id}/toggle
    @PatchMapping("/{id}/toggle")
    public TransferCorridorResponse toggleCorridor(@PathVariable("id") Long id) {
        return corridorService.toggleCorridor(id);
    }

    // DELETE /api/admin/corridors/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCorridor(@PathVariable("id") Long id) {
        corridorService.deleteCorridor(id);
    }
}