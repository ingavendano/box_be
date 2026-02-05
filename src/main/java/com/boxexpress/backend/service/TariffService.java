package com.boxexpress.backend.service;

import com.boxexpress.backend.model.TariffCategory;
import com.boxexpress.backend.model.TariffRange;
import com.boxexpress.backend.repository.TariffCategoryRepository;
import com.boxexpress.backend.repository.TariffRangeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TariffService {

    @Autowired
    private TariffCategoryRepository categoryRepository;

    @Autowired
    private TariffRangeRepository rangeRepository;

    @PostConstruct
    public void init() {
        // Initialize default categories if not present
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new TariffCategory("TARIFFS.TYPE.WEIGHT"));
            categoryRepository.save(new TariffCategory("TARIFFS.TYPE.VOLUME"));
            categoryRepository.save(new TariffCategory("TARIFFS.TYPE.PRODUCTS"));
        }
    }

    public List<TariffCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<TariffCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public TariffCategory saveCategory(TariffCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public TariffRange addRangeToCategory(Long categoryId, TariffRange range) {
        TariffCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        range.setCategory(category);
        return rangeRepository.save(range);
    }

    @Transactional
    public List<TariffRange> addRangesToCategory(Long categoryId, List<TariffRange> ranges) {
        TariffCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        for (TariffRange range : ranges) {
            range.setCategory(category);
            if (range.getMinValue() == null)
                range.setMinValue(java.math.BigDecimal.ZERO);
            if (range.getMaxValue() == null)
                range.setMaxValue(java.math.BigDecimal.ZERO);
        }
        return rangeRepository.saveAll(ranges);
    }

    @Transactional
    public void deleteRange(Long rangeId) {
        rangeRepository.deleteById(rangeId);
    }

    @Transactional
    public TariffRange updateRange(Long rangeId, TariffRange updatedRange) {
        return rangeRepository.findById(rangeId).map(existingRange -> {
            if (updatedRange.getSubcategoryName() != null) {
                existingRange.setSubcategoryName(updatedRange.getSubcategoryName());
            }
            if (updatedRange.getFeeValue() != null) {
                existingRange.setFeeValue(updatedRange.getFeeValue());
            }
            return rangeRepository.save(existingRange);
        }).orElseThrow(() -> new RuntimeException("Rango no encontrado"));
    }

    @Autowired
    private GlobalParameterService globalParameterService;

    public com.boxexpress.backend.dto.quote.QuoteResponse calculateQuote(
            com.boxexpress.backend.dto.quote.QuoteRequest request) {

        double weight = request.getWeight() != null ? request.getWeight() : 0.0;
        double declaredValue = request.getDeclaredValue() != null ? request.getDeclaredValue() : 0.0;
        boolean isHomeDelivery = request.getIsHomeDelivery() != null ? request.getIsHomeDelivery() : false;

        // Get Parameters
        double costLbLow = getParamDouble("COST_LB_LOW", 2.50);
        double costLbHigh = getParamDouble("COST_LB_HIGH", 2.00);
        double feeAirport = getParamDouble("FEE_AIRPORT", 5.00);
        double feeAdmin = getParamDouble("FEE_ADMIN", 2.50);
        double feeHome = getParamDouble("FEE_HOME_DELIVERY", 3.00);
        double taxIva = getParamDouble("TAX_IVA", 0.13);

        // 1. Base Cost
        double costPerLb = (weight <= 10) ? costLbLow : costLbHigh;
        double baseCost = weight * costPerLb;

        // 2. Tariff Cost
        double tariffCost = 0.0;
        if (request.getSubcategoryId() != null) {
            Optional<TariffRange> rangeOpt = rangeRepository.findById(request.getSubcategoryId());
            if (rangeOpt.isPresent()) {
                TariffRange range = rangeOpt.get();
                if (range.getFeeValue() != null) {
                    tariffCost = declaredValue * (range.getFeeValue().doubleValue() / 100.0);
                }
            }
        }

        // 3. Fees
        double adminFees = feeAirport + feeAdmin;

        // 4. Home Delivery
        double homeDeliveryFee = isHomeDelivery ? feeHome : 0.0;

        // 5. Taxes
        double taxes = (baseCost + adminFees + homeDeliveryFee) * taxIva;

        // 6. Total
        double totalCost = baseCost + tariffCost + adminFees + homeDeliveryFee + taxes;

        return com.boxexpress.backend.dto.quote.QuoteResponse.builder()
                .baseCost(baseCost)
                .tariffCost(tariffCost)
                .adminFees(adminFees)
                .homeDeliveryFee(homeDeliveryFee)
                .taxes(taxes)
                .totalCost(totalCost)
                .build();
    }

    private double getParamDouble(String key, double defaultValue) {
        try {
            return globalParameterService.getAllParameters().stream()
                    .filter(p -> p.getParamKey().equals(key))
                    .findFirst()
                    .map(p -> Double.parseDouble(p.getParamValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
