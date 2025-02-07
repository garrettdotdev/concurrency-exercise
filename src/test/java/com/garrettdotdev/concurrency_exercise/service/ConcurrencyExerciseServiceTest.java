package com.garrettdotdev.concurrency_exercise.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ConcurrencyExerciseServiceTest {

    private ConcurrencyExerciseService concurrencyExerciseService;

    @BeforeEach
    void setUp() {
        concurrencyExerciseService = new ConcurrencyExerciseService();
    }

    @AfterEach
    void clearDelayAfterTest() {
        concurrencyExerciseService.clearDelay();
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
    void testSemaphoreAllowsOnlyTwoConcurrentRequests() {
        CountDownLatch startLatch = new CountDownLatch(1);

        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            List<Future<ResponseEntity<String>>> futures = new ArrayList<>();

            futures.add(executorService.submit(() -> {
                concurrencyExerciseService.setDelay(500);
                startLatch.await();
                return concurrencyExerciseService.handleOne();
            }));
            futures.add(executorService.submit(() -> {
                concurrencyExerciseService.setDelay(500);
                startLatch.await();
                return concurrencyExerciseService.handleTwo();
            }));
            futures.add(executorService.submit(() -> {
                concurrencyExerciseService.setDelay(500);
                startLatch.await();
                return concurrencyExerciseService.handleThree();
            }));

            startLatch.countDown(); // Allow tasks to proceed concurrently

            int okCount = 0;
            int tmrCount = 0;

            for (Future<ResponseEntity<String>> future : futures) {
                ResponseEntity<String> response = getUnchecked(future);
                if (response.getStatusCode() == HttpStatus.OK) {
                    okCount++;
                } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    tmrCount++;
                }
            }

            assertEquals(2, okCount);
            assertEquals(1, tmrCount);
        }
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

    private ResponseEntity<String> getUnchecked(Future<ResponseEntity<String>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
