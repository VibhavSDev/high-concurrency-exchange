package com.exchange.inventory_service.controller;

import com.exchange.inventory_service.dto.PurchaseRequest;
import com.exchange.inventory_service.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<String> purchase(@Valid @RequestBody PurchaseRequest request) {
        purchaseService.processPurchase(request);
        return ResponseEntity.ok("Purchase request processed successfully");
    }
}
