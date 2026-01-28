package com.boxexpress.backend.controller;

import com.boxexpress.backend.model.TariffCategory;
import com.boxexpress.backend.model.TariffRange;
import com.boxexpress.backend.service.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    @Autowired
    private TariffService tariffService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only Admin should see config
    public List<TariffCategory> getAllCategories() {
        return tariffService.getAllCategories();
    }

    @PostMapping("/{categoryId}/ranges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TariffRange> addRange(@PathVariable Long categoryId, @RequestBody TariffRange range) {
        return ResponseEntity.ok(tariffService.addRangeToCategory(categoryId, range));
    }

    @PostMapping("/{categoryId}/ranges/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TariffRange>> addRangesBatch(@PathVariable Long categoryId,
            @RequestBody List<TariffRange> ranges) {
        return ResponseEntity.ok(tariffService.addRangesToCategory(categoryId, ranges));
    }

    @DeleteMapping("/ranges/{rangeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRange(@PathVariable Long rangeId) {
        tariffService.deleteRange(rangeId);
        return ResponseEntity.noContent().build();
    }
}
