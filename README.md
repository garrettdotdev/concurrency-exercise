# Concurrency Exercise
### A simple exercise in concurrency control in Java with Semaphore.

![Build Status](https://img.shields.io/github/actions/workflow/status/garrettdotdev/concurrency_exercise/ci.yml?branch=main)
![License](https://img.shields.io/github/license/garrettdotdev/concurrency_exercise)
![Coverage](https://img.shields.io/codecov/c/github/garrettdotdev/concurrency_exercise)
![Java](https://img.shields.io/badge/java-21-brightgreen)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.4.2-brightgreen)

## Table of Contents

## Overview

This exercise demonstrates how to control concurrency in Java using a `Semaphore`. The application simulates a shared resource with limited capacity and multiple threads trying to access it concurrently. The `Semaphore` ensures that only a fixed number of threads can access the resource at a time, preventing overload and resource exhaustion.

## What It Does

The application provides endpoints that simulate handling requests with concurrency control. It limits the number of concurrent requests to a shared resource using a `Semaphore`. The concurrency limit is hard-coded, but this limit could be specified in `application.properties`, instead.

## How It Works

- The `Semaphore` is initialized with a fixed number of permits.
- Each request to the shared resources attempts to acquire a permit.
- If a permit is available, the request proceeds; otherwise, a HTTP 429 (Too Many Requests) response is returned. (For real applications, you'd obviously want this behavior to be a bit more graceful.)
- After processing, the permit is released back to the `Semaphore`.

## Limitations

- This shows a very basic implementation, so there are many limitations (by definition).
- Uses a hard-coded concurrency limit.
- Does not handle dynamic adjustment of the number of permits.
- No GlobalExceptionHandler for error handling.

## Requirements

- Java 21
- Maven
- Spring Boot 3.4.2

## Build and Run

To build and run the application, use the following commands:

```shell
mvn clean install
mvn spring-boot:run
```

Or execute in your IDE of choice.

## Usage

The application exposes 3 endpoints:
- `/one`
- `/two`
- `/three`

When you send an empty GET request to these endpoints, they'll respond by telling you which one you hit. (You'll see what I mean; it's pretty obvious.)

## Error Handling

The codebase doesn't include any formal Exception handling; no GlobalExceptionHandler or anything like that, because showing how to do that isn't the point and this is a *very* simple application.

## Tests

There are two test classes: `ConcurrencyExerciseServiceTest` and `ConcurrencyExcerciseControllerTest`. You can run these by execeuting:

```shell
mvn clean test
```

I've also included Codecov config for CI out of habit and because I like the code coverage badge. (I mean, who doesn't?)

## Key Files & Details

- `ConcurrencyExerciseService`: The service class that performs request processing and concurrency control.
- `ConcurrencyExerciseController`: The controller class that exposes the endpoints.
- `ConcurrencyExerciseServiceTest`: JUnit test class for the service class.
- `ConcurrencyExerciseControllerTest`: JUnit test class for the controller class.