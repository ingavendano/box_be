package com.boxexpress.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;

@Entity
@Table(name = "tariff_ranges")
public class TariffRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private TariffCategory category;

    private String subcategoryName; // Optional descriptor

    @Column(nullable = false)
    private BigDecimal minValue;

    @Column(nullable = false)
    private BigDecimal maxValue;

    @Column(nullable = false)
    private BigDecimal feeValue; // Percentage or fixed amount

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TariffCategory getCategory() {
        return category;
    }

    public void setCategory(TariffCategory category) {
        this.category = category;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public BigDecimal getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(BigDecimal feeValue) {
        this.feeValue = feeValue;
    }
}
