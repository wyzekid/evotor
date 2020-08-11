package ru.evotor.processing.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.evotor.processing.clients.SecondLayerClientImpl;
import ru.evotor.processing.requests.RetryRequest;

import java.util.Objects;
import java.util.concurrent.BlockingDeque;


@Component
public class AvailableServiceListener implements ApplicationListener<ServiceAvailableEvent> {

    @Autowired
    private SecondLayerClientImpl secondLayerClient;

    @Override
    public void onApplicationEvent(ServiceAvailableEvent serviceAvailableEvent) {
        if (Objects.equals(serviceAvailableEvent.getAvailableServiceUrl(), secondLayerClient.getServiceUrl_1())) {
            processQueue(secondLayerClient.getBlockingQueue_1());
        } else if (Objects.equals(serviceAvailableEvent.getAvailableServiceUrl(), secondLayerClient.getServiceUrl_2())) {
            processQueue(secondLayerClient.getBlockingQueue_2());
        }
    }

    private void processQueue(BlockingDeque<RetryRequest> blockingDeque) {
        while (!blockingDeque.isEmpty()) {
            RetryRequest retryRequest = blockingDeque.peek();
            HttpStatus retryStatus = secondLayerClient.sendToOneService(retryRequest.getRequest(), retryRequest.getUrl(), HttpMethod.POST);
            if (retryStatus == HttpStatus.OK) {
                blockingDeque.remove(retryRequest);
            }
        }
    }
}
