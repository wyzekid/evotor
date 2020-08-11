package ru.evotor.processing.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие доступности сервиса
 */
@Setter
@Getter
public class ServiceAvailableEvent extends ApplicationEvent {

    private String availableServiceUrl;

    public ServiceAvailableEvent(Object source, String availableServiceUrl) {
        super(source);
        this.availableServiceUrl = availableServiceUrl;
    }
}
