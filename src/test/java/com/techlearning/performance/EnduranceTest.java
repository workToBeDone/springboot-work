package com.techlearning.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Endurance Test (Soak Test) - Tests application stability over extended period
 * Run with: mvn gatling:test -Dgatling.simulationClass=com.techlearning.performance.EnduranceTest
 *
 * WARNING: This test runs for 10+ minutes. Use for final validation only.
 */
public class EnduranceTest extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Scenario: Endurance Test
    ScenarioBuilder enduranceScenario = scenario("Endurance Test")
            .exec(
                    http("Get Version - Endurance")
                            .get("/api/v1/version")
                            .check(status().is(200))
            )
            .pause(Duration.ofSeconds(1));  // Pause to simulate real user behavior

    {
        setUp(
                enduranceScenario.injectOpen(
                        // Ramp up: 20 users over 60 seconds
                        rampUsers(20).during(Duration.ofSeconds(60)),
                        // Sustained load: 5 users/second for 10 minutes
                        constantUsersPerSec(5).during(Duration.ofMinutes(10))
                        // Optional: Extended duration for thorough testing
                        // constantUsersPerSec(5).during(Duration.ofMinutes(30))
                )
        ).protocols(httpProtocol)
                .assertions(
                        // Strict assertions - should maintain performance over time
                        global().responseTime().mean().lt(1000),          // Mean < 1 second throughout
                        global().responseTime().percentile(95.0).lt(1500), // 95th percentile < 1.5 seconds
                        global().successfulRequests().percent().gt(99.0),  // 99% success rate

                        // Check for performance degradation (memory leaks, etc.)
                        global().responseTime().max().lt(3000)            // No response should take > 3 seconds
                );
    }
}