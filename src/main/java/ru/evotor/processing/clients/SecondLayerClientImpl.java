package ru.evotor.processing.clients;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import ru.evotor.processing.requests.InputRequest;
import ru.evotor.processing.requests.RetryRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class SecondLayerClientImpl implements SecondLayerClient {

    @Getter
    @Value("${service_1.request.url}")
    private String serviceUrl_1;
    @Getter
    @Value("${service_2.request.url}")
    private String serviceUrl_2;
    @Value("${async.timeout}")
    private Integer asyncTimeout;
    @Value("${token}")
    private String token; //TODO сделал через параметр, чтоб не заморачиваться с сервисом аккаунтов

    private AsyncRestTemplate asyncRestTemplate;
    private RestTemplate restTemplate;

    @Getter
    private BlockingDeque<RetryRequest> blockingQueue_1;
    @Getter
    private BlockingDeque<RetryRequest> blockingQueue_2;


    @Autowired
    public SecondLayerClientImpl(@Value("${queue.max.size}") Integer queueSize) {
        asyncRestTemplate = new AsyncRestTemplate();
        restTemplate = new RestTemplate();
        blockingQueue_1 = new LinkedBlockingDeque<>(queueSize);
        blockingQueue_2 = new LinkedBlockingDeque<>(queueSize);
    }

    @Override
    public HttpStatus sendToAllServices(InputRequest inputRequest, HttpMethod httpMethod) {
        List<String> urls = Arrays.asList(serviceUrl_1, serviceUrl_2);
        for (String url: urls) {
            ListenableFuture<ResponseEntity<String>> futureResponse = asyncRestTemplate.exchange(
                    url,
                    httpMethod,
                    createRequest(inputRequest),
                    String.class
            );
            futureResponse.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
                @SneakyThrows
                @Override
                public void onFailure(Throwable throwable) {
                    addToRequestQueue(url, inputRequest);
                }

                @SneakyThrows
                @Override
                public void onSuccess(ResponseEntity<String> stringResponseEntity) {
                    ResponseEntity<String> response = futureResponse.get();
                    if (!response.getStatusCode().equals(HttpStatus.OK)) {
                        addToRequestQueue(url, inputRequest);
                    }
                }
            });
        }
        return HttpStatus.OK;
    }

    @Override
    public HttpStatus sendToOneService(InputRequest inputRequest, String requestUrl, HttpMethod httpMethod) {
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, httpMethod, createRequest(inputRequest), String.class);
        return response.getStatusCode();
    }

    private void addToRequestQueue(String requestUrl, InputRequest inputRequest) throws InterruptedException {
        if (Objects.equals(serviceUrl_1, requestUrl)) {
            blockingQueue_1.put(new RetryRequest(requestUrl, inputRequest));
        } else {
            blockingQueue_2.put(new RetryRequest(requestUrl, inputRequest));
        }
    }

    private HttpEntity<InputRequest> createRequest(InputRequest inputRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer" + " " + token);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return new HttpEntity<>(inputRequest, headers);
    }

}
