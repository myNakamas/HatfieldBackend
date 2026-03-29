package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.prices.Pricing;
import com.nakamas.hatfieldbackend.models.views.incoming.PricingView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PricingEvaluation;
import com.nakamas.hatfieldbackend.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/price")
public class PricingController {
    private final PricingService pricingService;

    @GetMapping("/evaluate")
    public PricingEvaluation evaluate(
            @RequestParam String deviceType,
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam String issue) {
        return pricingService.evaluate(deviceType, brand, model, issue);
    }

    @GetMapping("/worker/pricings")
    public List<PricingView> getAllPricings() {
        return pricingService.getAllPricings();
    }
    @GetMapping("/worker/pricings/filter")
    public List<PricingView> getPricingsWithFilters(
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model) {
        return pricingService.getPricingsWithFilters(deviceType, brand, model);
    }
    @PostMapping("/worker/pricings")
    public Pricing createPricing(@RequestBody PricingView pricing) {

        return pricingService.save(pricing);
    }

    @PutMapping("/worker/pricings/{id}")
    public Pricing updatePricing(@PathVariable Long id, @RequestBody PricingView pricing) {
        return pricingService.save(pricing, id);
    }

    @DeleteMapping("/worker/pricings/{id}")
    public void deletePricing(@PathVariable Long id) {
        pricingService.delete(id);
    }
    @GetMapping("/worker/pricings/csv")
    public ResponseEntity<byte[]> downloadCsv() {
        List<PricingView> pricings = pricingService.getAllPricings();
        byte[] csvBytes = pricingService.exportToCsv(pricings);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pricings.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @PostMapping("/worker/pricings/csv")
    public ResponseEntity<Void> uploadCsv(@RequestParam("file") MultipartFile file) {
        pricingService.importFromCsv(file);
        return ResponseEntity.ok().build();
    }
}