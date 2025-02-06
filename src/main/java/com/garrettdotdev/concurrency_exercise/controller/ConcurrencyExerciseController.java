package com.garrettdotdev.concurrency_exercise.controller;

import com.garrettdotdev.concurrency_exercise.service.ConcurrencyExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ConcurrencyExerciseController {

    private final ConcurrencyExerciseService concurrencyExerciseService;

    public ConcurrencyExerciseController(ConcurrencyExerciseService concurrencyExerciseService) {
        this.concurrencyExerciseService = concurrencyExerciseService;
    }

    @GetMapping("one")
    public ResponseEntity<String> endpointOne() {
        return concurrencyExerciseService.handleOne();
    }

    @GetMapping("two")
    public ResponseEntity<String> endpointTwo() {
        return concurrencyExerciseService.handleTwo();
    }

    @GetMapping("three")
    public ResponseEntity<String> endpointThree() {
        return concurrencyExerciseService.handleThree();
    }
}
