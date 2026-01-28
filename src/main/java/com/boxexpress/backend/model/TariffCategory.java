package com.boxexpress.backend.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tariff_categories")
public class TariffCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Translation Key e.g., TARIFFS.TYPE.WEIGHT

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TariffRange> ranges;

    public TariffCategory() {
    }

    public TariffCategory(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TariffRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<TariffRange> ranges) {
        this.ranges = ranges;
    }
}
