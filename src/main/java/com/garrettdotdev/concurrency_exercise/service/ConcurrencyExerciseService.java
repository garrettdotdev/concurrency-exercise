package com.garrettdotdev.concurrency_exercise.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

@Service
public class ConcurrencyExerciseService {

    private final Semaphore semaphore = new Semaphore(2, true);

    public ResponseEntity<String> handleOne() {
        return handleRequest(() -> "this is one");
    }

    public ResponseEntity<String> handleTwo() {
        return handleRequest(() -> "this is two");
    }

    public ResponseEntity<String> handleThree() {
        return handleRequest(() -> "this is three");
    }

    private ResponseEntity<String> handleRequest(RequestHandler handler) {
        if (semaphore.tryAcquire()) {
            try {
                return ResponseEntity.ok(handler.handle());
            } finally {
                semaphore.release();
            }
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Too Many Requests");
    }

    @FunctionalInterface
    interface RequestHandler {
        String handle();
    }
}
