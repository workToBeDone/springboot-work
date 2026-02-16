package com.techlearning.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Stress Test - Tests application under high load
 * Run with: mvn gatling:test -Dgatling.simulationClass=com.techlearning.performance.StressTest
 */
public class StressPerformance extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Scenario: Stress Test
    ScenarioBuilder stressScenario = scenario("Stress Test")
            .exec(
                    http("Get Version - Stress")
                            .get("/api/v1/version")
                            .check(status().is(200))
            )
            .pause(Duration.ofMillis(100));  // Small pause between requests

    {
        setUp(
                stressScenario.injectOpen(
                        // Ramp up quickly: 100 users in 20 seconds
                        rampUsers(100).during(Duration.ofSeconds(20)),
                        // High load: 50 users/second for 60 seconds
                        constantUsersPerSec(50).during(Duration.ofSeconds(60)),
                        // Peak load: 100 users/second for 30 seconds
                        constantUsersPerSec(100).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol)
                .assertions(
                        // More lenient assertions for stress test
                        global().responseTime().max().lt(5000),           // Max response < 5 seconds
                        global().responseTime().percentile(95.0).lt(3000), // 95th percentile < 3 seconds
                        global().successfulRequests().percent().gt(90.0),  // 90% success rate acceptable
                        global().requestsPerSec().gte(20.0)               // At least 20 requests/sec
                );
    }
}

