package com.exchange.inventory_service.service;

import com.exchange.inventory_service.dto.PurchaseRequest;
import com.exchange.inventory_service.exception.InsufficientStockException;
import com.exchange.inventory_service.exception.LockAcquisitionException;
import com.exchange.inventory_service.model.Outbox;
import com.exchange.inventory_service.model.Product;
import com.exchange.inventory_service.repository.OutboxRepository;
import com.exchange.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseService {

    private final ProductRepository productRepository;
    private final OutboxRepository outboxRepository;
    private final RedissonClient redissonClient;
    private final InventoryTransactionWorker inventoryTransactionWorker;

    public void processPurchase(PurchaseRequest request) {
        String lockKey = "lock:product:" + request.getProductId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if(lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                inventoryTransactionWorker.executeStockDetection(request);
                log.info("Purchase processed and committed successfully.");
            } else {
                throw new LockAcquisitionException("Could not acquire lock, system under high load");
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transaction interrupted");
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
