package com.exchange.inventory_service.worker;

import com.exchange.inventory_service.model.Outbox;
import com.exchange.inventory_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class OutboxRelayer {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    @Transactional
    public void relayMessages() {
        List<Outbox> pendingMessages = outboxRepository.findByProcessedFalse();

        if(pendingMessages.isEmpty()) return;

        log.info("Found {} pending messages in outbox. Relaying it to Kafka...", pendingMessages.size());

        for(Outbox message: pendingMessages) {
            try {
                ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(message.getPayload());
                jsonNode.put("eventId", message.getId());
                String finalizedPayload = objectMapper.writeValueAsString(jsonNode);

                kafkaTemplate.send(message.getTopic(), message.getAggregateId(), finalizedPayload);

                message.setProcessed(true);
                outboxRepository.save(message);

                log.info("Successfully relayed message ID: {}", message.getId());
            } catch (Exception e) {
                log.error("Failed to relay message ID: {}. Error: {}", message.getId(), e.getMessage());
            }
        }
    }
}
