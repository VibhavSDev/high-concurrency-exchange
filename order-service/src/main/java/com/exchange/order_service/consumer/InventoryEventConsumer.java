package com.exchange.order_service.consumer;

import com.exchange.order_service.dto.InventoryEvent;
import com.exchange.order_service.model.Order;
import com.exchange.order_service.model.OrderStatus;
import com.exchange.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "inventory-events", groupId = "order-group")
    private void consumeInventoryEvent(String message) {
        log.info("Received event raw string from Kafka highway: {}", message);
        try {
            InventoryEvent event = objectMapper.readValue(message, InventoryEvent.class);

            if (orderRepository.existsByEventId(event.getEventId())) {
                log.warn("Duplicate event detected! Event ID: {} has already been processed.", event.getEventId());
                return;
            }

            log.info("Processing fresh incoming transaction for Event ID: {}", event.getEventId());
            log.info("Successfully deserialized event for Product ID: {}", event.getProductId());

            Order order = Order.builder()
                    .eventId(event.getEventId())
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .orderStatus(OrderStatus.SUCCESS)
                    .build();

            orderRepository.save(order);
            log.info("Permanent Order receipt generated successfully in database with ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Critical failure parsing or processing incoming inventory event payload: ", e);
        }

    }
}
