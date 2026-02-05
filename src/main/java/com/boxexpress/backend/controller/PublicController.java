package com.boxexpress.backend.controller;

import com.boxexpress.backend.dto.quote.QuoteRequest;
import com.boxexpress.backend.dto.quote.QuoteResponse;
import com.boxexpress.backend.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final TariffService tariffService;

    @PostMapping("/quote")
    public ResponseEntity<QuoteResponse> calculateQuote(@RequestBody QuoteRequest request) {
        return ResponseEntity.ok(tariffService.calculateQuote(request));
    }
}
