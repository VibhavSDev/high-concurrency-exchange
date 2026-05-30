package com.exchange.inventory_service.config;

import com.exchange.inventory_service.model.Product;
import com.exchange.inventory_service.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            if(repository.count() == 0) {
                repository.saveAll(List.of(
                        new Product(null, "Nvidia RTX 5090", "GPU-5090", 10, 1999.99),
                        new Product(null, "MacBook Pro M4", "LAP-M4", 25, 2499.00),
                        new Product(null, "iPhone 17 Pro", "PHN-17P", 100, 1099.00)
                ));
                System.out.println("Seeded initial products to the database.");
            }
        };
    }
}
