package ru.evotor.processing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.evotor.processing.clients.SecondLayerClientImpl;
import ru.evotor.processing.requests.InputRequest;

@RestController
@RequestMapping("/api/v1/evotor/common")
public class CommonController {

    private final SecondLayerClientImpl client;

    @Autowired
    public CommonController(SecondLayerClientImpl client) {
        this.client = client;
    }

    @PostMapping("/inputEvent")
    public HttpStatus processInputEvent(@RequestBody InputRequest inputRequest) {
        return client.sendToAllServices(inputRequest, HttpMethod.POST);
    }
}
