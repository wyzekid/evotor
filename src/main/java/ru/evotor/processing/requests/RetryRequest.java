package ru.evotor.processing.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Запрос на эндпойнты второго слоя сервисов
 * в случае их доступности
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetryRequest {

    private String url;
    private InputRequest request;
}
