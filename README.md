# SpringBoot work

[![Maven Package](https://github.com/pbharambe/springboot-work/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/pbharambe/springboot-work/actions/workflows/maven-publish.yml)


## Swagger API details

[Springdoc-openapi (swagger)](https://springdoc.org/#swagger-ui-properties)


To enable Swagger OpenAPI add dependency
```xml
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>3.0.0</version>
    </dependency>
```

OpenAPI descriptions will be available at the path /v3/api-docs by default: ``http://localhost:8080/v3/api-docs/``

To use a custom path, we can indicate in the application.properties file:
``springdoc.api-docs.path=/api-docs``. Now api description will be access as ``http://localhost:8080/api-docs``.

Swagger UI will be available by default: ``http://localhost:8080/swagger-ui.html``
To customize the path of our API documentation. Modify our application.properties to include: ``springdoc.swagger-ui.path=/swagger-ui-custom.html``
So now our API documentation will be available at ``http://localhost:8080/swagger-ui-custom.html``

---

## Actuator

To enable Actuator include this spring dependency --
```xml

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
```
Custom actuator


---
## Quartz Scheduler

To enable Quartz Scheduler include this spring dependency --
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-quartz</artifactId>
    </dependency>
```

----
# Performance Testing
- ## Gatling Tests

To enable Gatling Tests include this spring dependency --
```xml
    <dependency>
        <groupId>io.gatling</groupId>
        <artifactId>gatling-app</artifactId>
        <version>3.9.0</version>
        <scope>test</scope>
    </dependency>
```
```xml
            <!-- Gatling Maven Plugin for Performance Testing -->
            <plugin>
                <groupId>io.gatling</groupId>
                <artifactId>gatling-maven-plugin</artifactId>
                <version>4.20.16</version>
                <configuration>
                    <!-- Run specific test -->
                    <!--<simulationClass>com.techlearning.performance.StressTest</simulationClass>-->
                    <!--<simulationClass>com.techlearning.performance.BasicLoadTest</simulationClass>-->
                    <!-- OR let it auto-discover -->
                    <runMultipleSimulations>true</runMultipleSimulations>

                    <failOnError>true</failOnError>
                </configuration>
            </plugin>
```

To run the tests, first start the application ``mvn spring-boot:run`` then use the command: ``mvn gatling:test``. This will execute all simulations in the src/test/ directory.

Test Scenarios:
- Basic Load Test - Ramp up to 50 users over 30 seconds
- Stress Test - 100+ concurrent users
- Spike Test - Sudden burst of 200 users
- Endurance Test - Sustained load over 10 minutes

Reports:
Gatling generates HTML reports in ``target/gatling/`` with:
- Response time distribution
- Requests per second
- Success/failure rates
- Detailed metrics and charts


- ## Simple Performance Tests
JUnit-based performance tests for quick validation.

Run Simple Performance Tests:
```bash
mvn test -Dtest=VersionApiPerformanceSimpleTest
```

Test Scenarios:
- Load test with 100 concurrent requests
- Response time validation (average < 500ms)
- Stress test with 500 requests
- Spike test with sudden burst

Key Metrics:
- Average response time
- Throughput (requests/second)
- Success rate percentage
- Min/Max response times



---

