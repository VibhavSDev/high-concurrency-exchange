package com.exchange.inventory_service.service;

import com.exchange.inventory_service.dto.PurchaseRequest;
import com.exchange.inventory_service.exception.InsufficientStockException;
import com.exchange.inventory_service.model.Outbox;
import com.exchange.inventory_service.model.Product;
import com.exchange.inventory_service.repository.OutboxRepository;
import com.exchange.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InventoryTransactionWorker {

    private final ProductRepository productRepository;
    private final OutboxRepository outboxRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeStockDetection(PurchaseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        productRepository.save(product);

        String payload = String.format("{\"productId\": %d, \"quantity\": %d, \"action\": \"PURCHASE\"}",
                request.getProductId(), request.getQuantity());

        Outbox outboxEntry = Outbox.builder()
                .aggregateId(product.getId().toString())
                .payload(payload)
                .topic("inventory-events")
                .build();

        outboxRepository.save(outboxEntry);
    }
}
