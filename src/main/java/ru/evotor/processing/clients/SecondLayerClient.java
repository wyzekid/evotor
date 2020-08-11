package ru.evotor.processing.clients;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import ru.evotor.processing.requests.InputRequest;

public interface SecondLayerClient {

    HttpStatus sendToAllServices(InputRequest request, HttpMethod httpMethod);

    HttpStatus sendToOneService(InputRequest request, String requestUrl, HttpMethod httpMethod);

}
