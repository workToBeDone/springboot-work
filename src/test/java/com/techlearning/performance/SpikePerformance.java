package com.techlearning.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Spike Test - Tests application response to sudden traffic spikes
 * Run with: mvn gatling:test -Dgatling.simulationClass=com.techlearning.performance.SpikeTest
 */
public class SpikePerformance extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Scenario: Spike Test
    ScenarioBuilder spikeScenario = scenario("Spike Test")
            .exec(
                    http("Get Version - Spike")
                            .get("/api/v1/version")
                            .check(status().is(200))
                            .check(responseTimeInMillis().lt(5000))  // Response within 5 seconds
            );

    {
        setUp(
                spikeScenario.injectOpen(
                        // Normal load: 10 users
                        rampUsers(10).during(Duration.ofSeconds(10)),
                        // Wait a bit
                        nothingFor(Duration.ofSeconds(5)),
                        // SPIKE: Sudden spike of 200 users at once
                        atOnceUsers(200),
                        // Hold the spike for 10 seconds
                        nothingFor(Duration.ofSeconds(10)),
                        // Recovery: Ramp down to normal
                        rampUsers(50).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol)
                .assertions(
                        // Very lenient for spike test - focus on recovery
                        global().responseTime().max().lt(10000),          // Max 10 seconds (during spike)
                        global().successfulRequests().percent().gt(85.0), // 85% success acceptable

                        // The system should handle the spike without complete failure
                        global().failedRequests().percent().lt(15.0)      // Less than 15% failures
                );
    }
}