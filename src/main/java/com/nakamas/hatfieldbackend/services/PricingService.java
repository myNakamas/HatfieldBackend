package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.prices.Pricing;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.incoming.PricingView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PricingEvaluation;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.BrandView;
import com.nakamas.hatfieldbackend.models.views.outgoing.inventory.ItemPropertyView;
import com.nakamas.hatfieldbackend.repositories.PricingRepository;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricingService {
    private final PricingRepository pricingRepository;
    private final InventoryItemService inventoryItemService;

    public PricingService(PricingRepository pricingRepository, InventoryItemService inventoryItemService) {
        this.pricingRepository = pricingRepository;
        this.inventoryItemService = inventoryItemService;
    }

    @Cacheable(value = "pricingEvaluation", key = "#deviceType + '_' + #brandId + '_' + #model + '_' + #issue")
    public PricingEvaluation evaluate(String deviceType, String brand, String model, String issue) {
        List<BrandView> brands = inventoryItemService.getAllBrands();
        Optional<BrandView> fromDb = brands.stream().filter(b -> b.value().equals(brand)).findAny();
        if (fromDb.isEmpty()) {
            return new PricingEvaluation(BigDecimal.ZERO, BigDecimal.ZERO, false, "CALL");
        }
        Optional<ItemPropertyView> modelFromDb = fromDb.get().models().stream().filter((m) -> m.value().equals(model)).findAny();
        if (modelFromDb.isEmpty()) {
            return new PricingEvaluation(BigDecimal.ZERO, BigDecimal.ZERO, false, "CALL");
        }
        Optional<Pricing> opt = pricingRepository.findByDeviceTypeAndBrandIdAndModelAndIssue(
                deviceType, fromDb.get().id(), modelFromDb.get().id(), issue);
        if (opt.isPresent()) {
            Pricing p = opt.get();
            return new PricingEvaluation(p.getPrice(), p.getOriginalPrice(), true, "VISIT_SHOP");
        }
        return new PricingEvaluation(BigDecimal.ZERO, BigDecimal.ZERO, false, "CALL");
    }

    public List<PricingView> getAllPricings() {
        return pricingRepository.findAllPricingViews();
    }
    public List<PricingView> getPricingsWithFilters(String deviceType, String brand, String model) {
        return pricingRepository.findAllWithFilters(deviceType, brand, model);
    }

    public Pricing save(PricingView view) {
        Pricing pricing = toEntity(view);
        return save(pricing);
    }

    public Pricing save(PricingView view, Long id) {
        Pricing pricing = toEntity(view);
        pricing.setId(id);
        return save(pricing);
    }

    private Pricing toEntity(PricingView view) {
        Brand brand = inventoryItemService.getOrCreateBrand(view.brand());
        Model model = inventoryItemService.getOrCreateModel(view.model(), brand);
        return new Pricing(view, brand, model);
    }

    @CacheEvict(value = "pricingEvaluation", allEntries = true)
    public Pricing save(Pricing pricing) {
        if (pricing.getId() == null) {
            Optional<Pricing> fromDb = pricingRepository.findByDeviceTypeAndBrandIdAndModelAndIssue(
                    pricing.getDeviceType(), pricing.getBrandId(), pricing.getModelId(), pricing.getIssue());
            fromDb.ifPresent((p) -> pricing.setId(p.getId()));
        }
        return pricingRepository.save(pricing);
    }

    @CacheEvict(value = "pricingEvaluation", allEntries = true)
    public void delete(Long id) {
        pricingRepository.deleteById(id);
    }

    // CSV Export
    public byte[] exportToCsv(List<PricingView> pricings) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            // Header
            csvWriter.writeNext(new String[]{"deviceType", "brand", "model", "issue", "price", "originalPrice"});

            // Data
            for (PricingView p : pricings) {
                csvWriter.writeNext(new String[]{
                        p.deviceType(),
                        p.brand(),
                        p.model(),
                        p.issue(),
                        p.price().toString(),
                        p.originalPrice() != null ? p.originalPrice().toString() : ""
                });
            }
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }

    // CSV Import
    @CacheEvict(value = "pricingEvaluation", allEntries = true)
    public void importFromCsv(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CsvToBean<PricingView> csvToBean = new CsvToBeanBuilder<PricingView>(reader)
                    .withType(PricingView.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(',')
                    .build();

            List<PricingView> imported = csvToBean.parse();

            List<Pricing> collect = imported.stream().map(this::toEntity).collect(Collectors.toList());

            pricingRepository.saveAll(collect);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import CSV: " + e.getMessage(), e);
        }
    }
}