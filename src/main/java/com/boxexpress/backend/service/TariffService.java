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
}
