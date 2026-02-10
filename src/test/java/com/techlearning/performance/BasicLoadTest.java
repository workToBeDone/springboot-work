package com.techlearning.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Basic Load Test - Tests normal application load
 * Run with: mvn gatling:test -Dgatling.simulationClass=com.techlearning.performance.BasicLoadTest
 */
public class BasicLoadTest extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Performance Test");

    // Scenario: Basic Load Test
    ScenarioBuilder basicLoadScenario = scenario("Basic Load Test")
            .exec(
                    http("Get Version")
                            .get("/api/v1/version")
                            .check(status().is(200))
                            .check(jsonPath("$.version").exists())
                            .check(jsonPath("$.currentDate").exists())
                            .check(jsonPath("$.applicationName").exists())
            );

    {
        setUp(
                basicLoadScenario.injectOpen(
                        // Warm up: 5 users
                        rampUsers(5).during(Duration.ofSeconds(10)),
                        // Ramp up: gradually increase to 50 users over 30 seconds
                        rampUsers(50).during(Duration.ofSeconds(30)),
                        // Sustained load: 10 users/second for 60 seconds
                        constantUsersPerSec(10).during(Duration.ofSeconds(60))
                )
        ).protocols(httpProtocol)
                .assertions(
                        // Response time assertions
                        global().responseTime().max().lt(2000),          // Max response < 2 seconds
                        global().responseTime().mean().lt(500),          // Mean response < 500ms
                        global().responseTime().percentile(95.0).lt(1000), // 95th percentile < 1 second

                        // Success rate assertions
                        global().successfulRequests().percent().gt(95.0), // 95% success rate

                        // Request count assertion
                        global().requestsPerSec().gte(5.0)               // At least 5 requests/sec
                );
    }
}