package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.ThirdParty;
import com.example.tpcm_spring.service.app.ThirdPartyServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("app.ThirdPartyController")
@RequestMapping("/api/app/thirdparties")
@RequiredArgsConstructor
public class ThirdPartyController {

    private final ThirdPartyServiceApp thirdPartyService;

    @PostMapping
    public ResponseEntity<ThirdParty> createThirdParty(@RequestBody ThirdParty thirdParty) {
        ThirdParty saved = thirdPartyService.createThirdParty(thirdParty);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<ThirdParty>> getAllThirdParties() {
        return ResponseEntity.ok(thirdPartyService.getAllThirdParties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThirdParty> getThirdPartyById(@PathVariable Long id) {
        ThirdParty thirdParty = thirdPartyService.getThirdPartyById(id);
        return (thirdParty != null) ? ResponseEntity.ok(thirdParty) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ThirdParty> updateThirdParty(@PathVariable Long id, @RequestBody ThirdParty updated) {
        ThirdParty updatedThirdParty = thirdPartyService.updateThirdParty(id, updated);
        return (updatedThirdParty != null) ? ResponseEntity.ok(updatedThirdParty) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThirdParty(@PathVariable Long id) {
        thirdPartyService.deleteThirdParty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-active")
    public ResponseEntity<List<ThirdParty>> getThirdPartyByActive(@RequestParam String active) {
        List<ThirdParty> results = thirdPartyService.findByActive(active);
        return results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }

    @GetMapping("/search/by-service-type")
    public ResponseEntity<List<ThirdParty>> getThirdPartiesByServiceType(@RequestParam String serviceType) {
        List<ThirdParty> results = thirdPartyService.findByServiceType(serviceType);
        return results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }
}
