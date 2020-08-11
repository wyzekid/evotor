package ru.evotor.processing.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.evotor.processing.clients.SecondLayerClient;
import ru.evotor.processing.events.AvailableServicePublisher;

/**
 * Компонент проверки доступности сервисов
 * второго слоя
 */
@Component
public class HealthChecker {

    @Value("${service_1.health.url}")
    private String healthCheckUrl_1;
    @Value("${service_2.health.url}")
    private String healthCheckUrl_2;
    @Value("${service_1.request.url}")
    private String serviceUrl_1;
    @Value("${service_2.request.url}")
    private String serviceUrl_2;

    @Autowired
    private SecondLayerClient secondLayerClient;
    @Autowired
    private AvailableServicePublisher availableServicePublisher;

    @Scheduled(fixedRate = 300_000)
    public void scheduleHealthCheck() {
        HttpStatus firstCheck = secondLayerClient.sendToOneService(null, healthCheckUrl_1, HttpMethod.GET);
        if (firstCheck == HttpStatus.OK) {
            availableServicePublisher.publishAvailableEvent(serviceUrl_1);
        }
        HttpStatus secondCheck = secondLayerClient.sendToOneService(null, healthCheckUrl_2, HttpMethod.GET);
        if (secondCheck == HttpStatus.OK) {
            availableServicePublisher.publishAvailableEvent(serviceUrl_2);
        }
    }

}
