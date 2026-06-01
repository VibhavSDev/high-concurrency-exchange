package com.exchange.gateway_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @PostMapping("/inventory-unavailable")
    public ResponseEntity<Map<String, Object>> handleInventoryFallback() {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "Service Temporarily Unavailable");
        fallbackResponse.put("message", "The checkout system is currently overloaded or undergoing self-healing. Please try again later.");
        fallbackResponse.put("status", 503);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
}
