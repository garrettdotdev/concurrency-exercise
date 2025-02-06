package com.garrettdotdev.concurrency_exercise.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConcurrencyExerciseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testEndpointOneReturnsCorrectResponse() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/one", String.class);
        assertEquals("this is one", response.getBody());
    }

    @Test
    void testEndpointTwoReturnsCorrectResponse() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/two", String.class);
        assertEquals("this is two", response.getBody());
    }

    @Test
    void testEndpointThreeReturnsCorrectResponse() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl() + "/three", String.class);
        assertEquals("this is three", response.getBody());
    }

}
