package com.exchange.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryEvent {
    private Long eventId;
    private Long productId;
    private Integer quantity;
    private String status;
}
