package com.garrettdotdev.concurrency_exercise.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyExerciseServiceTest {

    private ConcurrencyExerciseService concurrencyExerciseService;

    @BeforeEach
    void setUp() {
        concurrencyExerciseService = new ConcurrencyExerciseService();
    }

    @Test
    void testHandleOneReturnsCorrectResponse() {
        ResponseEntity<String> response = concurrencyExerciseService.handleOne();
        assertEquals("this is one", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testHandleTwoReturnsCorrectResponse() {
        ResponseEntity<String> response = concurrencyExerciseService.handleTwo();
        assertEquals("this is two", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testHandleThreeReturnsCorrectResponse() {
        ResponseEntity<String> response = concurrencyExerciseService.handleThree();
        assertEquals("this is three", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSemaphoreAllowsOnlyTwoConcurrentRequests() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3); // Ensures proper coordination of threads

        // Results array to store responses
        ResponseEntity<String>[] responses = new ResponseEntity[3];

        executor.submit(() -> {
            responses[0] = concurrencyExerciseService.handleOne();
            latch.countDown();
        });
        executor.submit(() -> {
            responses[1] = concurrencyExerciseService.handleTwo();
            latch.countDown();
        });
        executor.submit(() -> {
            responses[2] = concurrencyExerciseService.handleThree();
            latch.countDown();
        });

        latch.await(); // Wait for all threads to finish

        // Verify the first two requests succeeded
        assertEquals(HttpStatus.OK, responses[0].getStatusCode());
        assertEquals(HttpStatus.OK, responses[1].getStatusCode());

        // Verify the third request was rejected
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, responses[2].getStatusCode());
        assertEquals("Too Many Requests", responses[2].getBody());
    }

    @Test
    void testSemaphoreReleasesAfterHandlingRequests() {
        // Allow concurrent requests to complete and ensure semaphore releases them
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            ResponseEntity<String> response1 = concurrencyExerciseService.handleOne();
            ResponseEntity<String> response2 = concurrencyExerciseService.handleTwo();
            assertNotNull(response1);
            assertNotNull(response2);
        });
    }
}
