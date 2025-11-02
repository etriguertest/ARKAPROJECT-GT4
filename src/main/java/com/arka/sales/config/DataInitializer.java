package com.arka.sales.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @PostConstruct
    public void info() {
        log.info("Ensure PostgreSQL is up and schema.sql will be executed (spring.sql.init.mode=always)");
    }
}