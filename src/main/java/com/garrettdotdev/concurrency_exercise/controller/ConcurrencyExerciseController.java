package com.garrettdotdev.concurrency_exercise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ConcurrencyExerciseController {

    @GetMapping("one")
    public String endpointOne() {
        return "this is one";
    }

    @GetMapping("two")
    public String endpointTwo() {
        return "this is two";
    }

    @GetMapping("three")
    public String endpointThree() {
        return "this is three";
    }
}
