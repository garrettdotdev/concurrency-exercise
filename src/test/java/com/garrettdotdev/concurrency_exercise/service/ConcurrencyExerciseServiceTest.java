package com.garrettdotdev.concurrency_exercise.service;

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
    void testSemaphoreAllowsOnlyTwoConcurrentRequests() throws InterruptedException, ExecutionException {
        concurrencyExerciseService.setDelay(1000);

        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            CountDownLatch latch = new CountDownLatch(1);

            List<Callable<ResponseEntity<String>>> tasks = List.of(
                () -> {
                    latch.await();
                    return concurrencyExerciseService.handleOne();
                },
                () -> {
                    latch.await();
                    return concurrencyExerciseService.handleTwo();
                },
                () -> {
                    latch.await();
                    return concurrencyExerciseService.handleThree();
                }
            );

            List<Future<ResponseEntity<String>>> futures = tasks.stream()
                .map(executorService::submit)
                .toList();

            latch.countDown();

            Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<ResponseEntity<String>> responses = futures.stream()
                    .map(this::getUnchecked)
                    .toList();

                assertEquals(HttpStatus.OK, responses.get(0).getStatusCode());
                assertEquals(HttpStatus.OK, responses.get(1).getStatusCode());

                assertEquals(HttpStatus.TOO_MANY_REQUESTS, responses.get(2).getStatusCode());
                assertEquals("Too Many Requests", responses.get(2).getBody());
            });
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
