package com.exchange.inventory_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId;
    private String payload;
    private String topic;

    @Builder.Default
    private Boolean processed = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
