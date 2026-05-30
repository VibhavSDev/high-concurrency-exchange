package com.exchange.inventory_service.repository;

import com.exchange.inventory_service.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    List<Outbox> findByProcessedFalse();
}
