package com.okanetransfer.controller;



import com.okanetransfer.dto.request.AgencyRequest;
import com.okanetransfer.dto.response.AgencyResponse;
import com.okanetransfer.service.AgencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agencies")
public class AgencyController {

    private static final Logger log = LoggerFactory.getLogger(AgencyController.class);

    @Autowired
    private AgencyService agencyService;

    @GetMapping
    public List<AgencyResponse> getAllAgencies() {
        return agencyService.getAllAgencies();
    }

    @GetMapping("/active")
    public List<AgencyResponse> getActiveAgencies() {
        return agencyService.getActiveAgencies();
    }

    @GetMapping("/{id}")
    public AgencyResponse getAgency(@PathVariable("id") Long id) {
        return agencyService.getAgency(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgencyResponse createAgency(@RequestBody AgencyRequest request) {
        return agencyService.createAgency(request);
    }

    @PutMapping("/{id}")
    public AgencyResponse updateAgency(@PathVariable("id") Long id, @RequestBody AgencyRequest request) {
        return agencyService.updateAgency(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAgency(@PathVariable("id") Long id) {
        agencyService.deactivateAgency(id);
    }
    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean activateAgency(@PathVariable("id") Long id) {
        return agencyService.activateAgency(id);
    }
}