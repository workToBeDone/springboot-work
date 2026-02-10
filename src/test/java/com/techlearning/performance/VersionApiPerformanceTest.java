package com.techlearning.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.responseTimeInMillis;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class VersionApiPerformanceTest extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Scenario 1: Basic Load Test
    ScenarioBuilder basicLoadTest = scenario("Basic Load Test")
            .exec(
                    http("Get Version")
                            .get("/api/v1/version")
                            .check(status().is(200))
                            .check(jsonPath("$.version").exists())
                            .check(jsonPath("$.currentDate").exists())
                            .check(jsonPath("$.applicationName").exists())
            );

    // Scenario 2: Stress Test
    ScenarioBuilder stressTest = scenario("Stress Test")
            .exec(
                    http("Get Version - Stress")
                            .get("/api/v1/version")
                            .check(status().is(200))
            )
            .pause(Duration.ofMillis(100));

    // Scenario 3: Spike Test
    ScenarioBuilder spikeTest = scenario("Spike Test")
            .exec(
                    http("Get Version - Spike")
                            .get("/api/v1/version")
                            .check(status().is(200))
                            .check(responseTimeInMillis().lt(1000))
            );

    // Scenario 4: Endurance Test
    ScenarioBuilder enduranceTest = scenario("Endurance Test")
            .exec(
                    http("Get Version - Endurance")
                            .get("/api/v1/version")
                            .check(status().is(200))
            )
            .pause(Duration.ofSeconds(1));

    {
        // Test 1: Ramp up users gradually (Load Test)
        setUp(
                basicLoadTest.injectOpen(
                        rampUsers(50).during(Duration.ofSeconds(30)),
                        constantUsersPerSec(10).during(Duration.ofSeconds(60))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().max().lt(2000),
                global().successfulRequests().percent().gt(95.0)
        );

        // Test 2: Stress Test - High concurrent users



        // Uncomment other scenarios as needed:

        /*
        // Test 2: Stress Test - High concurrent users
        setUp(
                stressTest.injectOpen(
                        rampUsers(100).during(Duration.ofSeconds(20)),
                        constantUsersPerSec(50).during(Duration.ofSeconds(60))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().percentile(95.0).lt(3000),
                global().successfulRequests().percent().gt(90.0)
        );
        */

        /*
        // Test 3: Spike Test - Sudden load increase
        setUp(
                spikeTest.injectOpen(
                        nothingFor(Duration.ofSeconds(5)),
                        atOnceUsers(200),
                        nothingFor(Duration.ofSeconds(10)),
                        rampUsers(50).during(Duration.ofSeconds(30))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().max().lt(5000),
                global().successfulRequests().percent().gt(85.0)
        );
        */

        /*
        // Test 4: Endurance Test - Sustained load
        setUp(
                enduranceTest.injectOpen(
                        rampUsers(20).during(Duration.ofSeconds(60)),
                        constantUsersPerSec(5).during(Duration.ofMinutes(10))
                ).protocols(httpProtocol)
        ).assertions(
                global().responseTime().mean().lt(1000),
                global().successfulRequests().percent().gt(99.0)
        );
        */
    }
}
