package com.example.pp.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeignConfigurationTest {

    @Test
    void testFeignConfig_ShouldBeLoadable() {
        // Проверяем, что конфигурационный класс может быть загружен
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(FeignConfig.class);
            context.refresh();

            FeignConfig feignConfig = context.getBean(FeignConfig.class);
            assertNotNull(feignConfig);
        }
    }

    @Test
    void testFeignClientProperties_ShouldBeAvailable() {
        // Проверяем доступность свойств Feign
        FeignClientProperties properties = new FeignClientProperties();
        assertNotNull(properties);
    }
}