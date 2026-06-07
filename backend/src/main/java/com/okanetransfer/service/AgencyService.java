package com.okanetransfer.service;

import com.okanetransfer.dto.request.AgencyRequest;
import com.okanetransfer.dto.response.AgencyResponse;
import com.okanetransfer.entity.Agency;
import com.okanetransfer.entity.User;
import com.okanetransfer.repository.AgencyRepository;
import com.okanetransfer.exception.InvalidDailyLimitException;
import com.okanetransfer.exception.AgencyNotFoundException;
import com.okanetransfer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyService {

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private UserRepository userRepository;

    public List<AgencyResponse> getAllAgencies() {
        return agencyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AgencyResponse> getActiveAgencies() {
        return agencyRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AgencyResponse getAgency(Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new AgencyNotFoundException("Agency not found"));
        return toResponse(agency);
    }

    public AgencyResponse createAgency(AgencyRequest request) {
        if (request.getDailyLimit() == null || request.getDailyLimit().doubleValue() <= 0) {
            throw new InvalidDailyLimitException("Daily limit must be greater than 0");
        }
        Agency agency = toEntity(request);
        return toResponse(agencyRepository.save(agency));
    }

    public AgencyResponse updateAgency(Long id, AgencyRequest request) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new AgencyNotFoundException("Agency not found"));

        if (request.getName() != null) agency.setName(request.getName());
        if (request.getAddress() != null) agency.setAddress(request.getAddress());
        if (request.getCountry() != null) agency.setCountry(request.getCountry());
        if (request.getDailyLimit() != null) {
            if (request.getDailyLimit().doubleValue() <= 0) {
                throw new InvalidDailyLimitException("Daily limit must be greater than 0");
            }
            agency.setDailyLimit(request.getDailyLimit());
        }
        if (request.getManagerId() != null && request.getManagerId() > 0) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            agency.setManager(manager);
        }
        agency.setActive(request.isActive());

        return toResponse(agencyRepository.save(agency));
    }

    public void deleteAgency(Long id) {
        if (!agencyRepository.existsById(id)) {
            throw new AgencyNotFoundException("Agency not found");
        }
        agencyRepository.deleteById(id);
    }

    // ---- Mappers ----
    private AgencyResponse toResponse(Agency agency) {
        AgencyResponse response = new AgencyResponse();
        response.setId(agency.getId());
        response.setName(agency.getName());
        response.setAddress(agency.getAddress());
        response.setCountry(agency.getCountry());
        response.setDailyLimit(agency.getDailyLimit());
        response.setActive(agency.isActive());
        response.setCreatedAt(agency.getCreatedAt());
        if (agency.getManager() != null) {
            response.setManagerName(agency.getManager().getFullName());
        }
        return response;
    }

    private Agency toEntity(AgencyRequest request) {
        Agency agency = new Agency();
        agency.setName(request.getName());
        agency.setAddress(request.getAddress());
        agency.setCountry(request.getCountry());
        agency.setDailyLimit(request.getDailyLimit());
        agency.setActive(request.isActive());
        if (request.getManagerId() != null && request.getManagerId() > 0) {
            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            agency.setManager(manager);
        }
        return agency;
    }
}