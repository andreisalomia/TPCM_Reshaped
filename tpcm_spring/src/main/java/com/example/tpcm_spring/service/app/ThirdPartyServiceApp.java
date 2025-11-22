package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.ThirdParty;
import com.example.tpcm_spring.repository.app.ThirdPartyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyServiceApp {

    private final ThirdPartyRepository thirdPartyRepository;

    private void validateThirdParty(ThirdParty tp) {
        if (tp.getName() == null || !tp.getName().matches("^[a-zA-Z0-9 ]{1,100}$")) {
            log.warn("Invalid third-party name: {}", tp.getName());
            throw new ValidationException("Name must be alphanumeric and up to 100 characters");
        }

        if (tp.getServiceType() == null || !tp.getServiceType().matches("^[a-zA-Z ]{1,50}$")) {
            log.warn("Invalid service type: {}", tp.getServiceType());
            throw new ValidationException("Service type must be alphabetic and up to 50 characters");
        }

        if (tp.getEmail() != null && !tp.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            log.warn("Invalid email format: {}", tp.getEmail());
            throw new ValidationException("Email format is invalid");
        }

        if (tp.getActive() == null || !(tp.getActive().equals("Y") || tp.getActive().equals("N"))) {
            log.warn("Invalid active flag: {}", tp.getActive());
            throw new ValidationException("Active must be either 'Y' or 'N'");
        }
    }

    @Transactional
    public ThirdParty createThirdParty(ThirdParty thirdParty) {
        validateThirdParty(thirdParty);
        ThirdParty saved = thirdPartyRepository.save(thirdParty);
        log.info("Created third party with ID: {}", saved.getThirdPartyID());
        return saved;
    }

    public List<ThirdParty> getAllThirdParties() {
        log.info("Fetching all third parties");
        return thirdPartyRepository.findAll();
    }

    public ThirdParty getThirdPartyById(Long id) {
        return thirdPartyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Third party with ID " + id + " not found"));
    }

    @Transactional
    public ThirdParty updateThirdParty(Long id, ThirdParty updated) {
        ThirdParty existing = thirdPartyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Third party with ID " + id + " not found"));

        validateThirdParty(updated);

        existing.setName(updated.getName());
        existing.setServiceType(updated.getServiceType());
        existing.setEmail(updated.getEmail());
        existing.setActive(updated.getActive());

        ThirdParty saved = thirdPartyRepository.save(existing);
        log.info("Updated third party with ID: {}", saved.getThirdPartyID());
        return saved;
    }

    @Transactional
    public void deleteThirdParty(Long id) {
        if (!thirdPartyRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing third party with ID: {}", id);
            throw new NotFoundException("Third party with ID " + id + " not found");
        }
        thirdPartyRepository.deleteById(id);
        log.info("Deleted third party with ID: {}", id);
    }

    public List<ThirdParty> findByActive(String active) {
        if (!active.equals("Y") && !active.equals("N")) {
            log.warn("Invalid active flag: {}", active);
            throw new ValidationException("Active must be 'Y' or 'N'");
        }
        log.info("Finding third parties with active status: {}", active);
        return thirdPartyRepository.findByActive(active);
    }

    public List<ThirdParty> findByServiceType(String serviceType) {
        if (serviceType == null || serviceType.isBlank()) {
            log.warn("Service type must not be blank");
            throw new ValidationException("Service type must not be blank");
        }
        log.info("Finding third parties with service type: {}", serviceType);
        return thirdPartyRepository.findByServiceTypeIgnoreCase(serviceType);
    }
}
