/*
package com.techlearning.performance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Version API Performance Tests")
class VersionApiPerformanceSimpleTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Load Test - 100 concurrent requests")
    void testConcurrentRequests_100Users() throws InterruptedException, ExecutionException {
        // Arrange
        int numberOfRequests = 100;
        String url = "http://localhost:" + port + "/api/v1/version";
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<ResponseEntity<String>>> futures = new ArrayList<>();

        Instant start = Instant.now();

        // Act
        for (int i = 0; i < numberOfRequests; i++) {
            Future<ResponseEntity<String>> future = executor.submit(() ->
                    restTemplate.getForEntity(url, String.class)
            );
            futures.add(future);
        }

        // Wait for all requests to complete
        int successCount = 0;
        List<Long> responseTimes = new ArrayList<>();

        for (Future<ResponseEntity<String>> future : futures) {
            Instant requestStart = Instant.now();
            ResponseEntity<String> response = future.get();
            long responseTime = Duration.between(requestStart, Instant.now()).toMillis();
            responseTimes.add(responseTime);

            if (response.getStatusCode() == HttpStatus.OK) {
                successCount++;
            }
        }

        Instant end = Instant.now();
        long totalDuration = Duration.between(start, end).toMillis();

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Assert
        System.out.println("=== Load Test Results (100 requests) ===");
        System.out.println("Total Duration: " + totalDuration + "ms");
        System.out.println("Success Rate: " + (successCount * 100.0 / numberOfRequests) + "%");
        System.out.println("Throughput: " + (numberOfRequests * 1000.0 / totalDuration) + " req/sec");

        assertTrue(successCount >= 95, "Success rate should be at least 95%");
    }

    @Test
    @DisplayName("Response Time Test - Average under 500ms")
    void testResponseTime_AverageUnder500ms() {
        // Arrange
        String url = "http://localhost:" + port + "/api/v1/version";
        int numberOfRequests = 50;
        List<Long> responseTimes = new ArrayList<>();

        // Act
        for (int i = 0; i < numberOfRequests; i++) {
            Instant start = Instant.now();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            long responseTime = Duration.between(start, Instant.now()).toMillis();
            responseTimes.add(responseTime);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        // Calculate statistics
        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        // Assert
        System.out.println("=== Response Time Test Results ===");
        System.out.println("Average Response Time: " + avgResponseTime + "ms");
        System.out.println("Min Response Time: " + minResponseTime + "ms");
        System.out.println("Max Response Time: " + maxResponseTime + "ms");

        assertTrue(avgResponseTime < 500, "Average response time should be under 500ms");
    }

    @Test
    @DisplayName("Stress Test - 500 requests with limited threads")
    void testStressTest_500Requests() throws InterruptedException {
        // Arrange
        int numberOfRequests = 500;
        String url = "http://localhost:" + port + "/api/v1/version";
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);

        int[] successCount = {0};
        int[] failureCount = {0};

        Instant start = Instant.now();

        // Act
        IntStream.range(0, numberOfRequests).forEach(i ->
                executor.submit(() -> {
                    try {
                        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                        if (response.getStatusCode() == HttpStatus.OK) {
                            synchronized (successCount) {
                                successCount[0]++;
                            }
                        } else {
                            synchronized (failureCount) {
                                failureCount[0]++;
                            }
                        }
                    } catch (Exception e) {
                        synchronized (failureCount) {
                            failureCount[0]++;
                        }
                    } finally {
                        latch.countDown();
                    }
                })
        );

        latch.await(2, TimeUnit.MINUTES);
        Instant end = Instant.now();
        long totalDuration = Duration.between(start, end).toMillis();

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Assert
        System.out.println("=== Stress Test Results (500 requests) ===");
        System.out.println("Total Duration: " + totalDuration + "ms");
        System.out.println("Successful Requests: " + successCount[0]);
        System.out.println("Failed Requests: " + failureCount[0]);
        System.out.println("Success Rate: " + (successCount[0] * 100.0 / numberOfRequests) + "%");
        System.out.println("Throughput: " + (numberOfRequests * 1000.0 / totalDuration) + " req/sec");

        assertTrue(successCount[0] >= 450, "At least 90% of requests should succeed");
    }

    @Test
    @DisplayName("Spike Test - Sudden burst of requests")
    void testSpikeTest_SuddenLoad() throws InterruptedException, ExecutionException {
        // Arrange
        int burstSize = 200;
        String url = "http://localhost:" + port + "/api/v1/version";
        ExecutorService executor = Executors.newFixedThreadPool(50);
        List<Future<ResponseEntity<String>>> futures = new ArrayList<>();

        // Act - Create sudden spike
        Instant start = Instant.now();

        for (int i = 0; i < burstSize; i++) {
            Future<ResponseEntity<String>> future = executor.submit(() ->
                    restTemplate.getForEntity(url, String.class)
            );
            futures.add(future);
        }

        int successCount = 0;
        for (Future<ResponseEntity<String>> future : futures) {
            try {
                ResponseEntity<String> response = future.get(30, TimeUnit.SECONDS);
                if (response.getStatusCode() == HttpStatus.OK) {
                    successCount++;
                }
            } catch (TimeoutException e) {
                System.err.println("Request timed out");
            }
        }

        Instant end = Instant.now();
        long totalDuration = Duration.between(start, end).toMillis();

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Assert
        System.out.println("=== Spike Test Results (200 concurrent requests) ===");
        System.out.println("Total Duration: " + totalDuration + "ms");
        System.out.println("Success Rate: " + (successCount * 100.0 / burstSize) + "%");

        assertTrue(successCount >= 170, "At least 85% of spike requests should succeed");
    }
}
*/
