package ru.evotor.processing.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AvailableServicePublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishAvailableEvent(final String availableServiceUrl) {
        ServiceAvailableEvent availableEvent = new ServiceAvailableEvent(this, availableServiceUrl);
        applicationEventPublisher.publishEvent(availableEvent);
    }

}
