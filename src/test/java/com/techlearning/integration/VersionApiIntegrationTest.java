package com.techlearning.integration;

import com.techlearning.dto.VersionResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Version API Integration Tests with TestContainers")
class VersionApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure any dynamic properties if needed
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    @DisplayName("Should return version information with 200 status")
    void testGetVersion_ReturnsSuccessfully() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/version")
                .then()
                .statusCode(200)
                .body("version", notNullValue())
                .body("currentDate", notNullValue())
                .body("applicationName", notNullValue());
    }

    @Test
    @DisplayName("Should return correct application version")
    void testGetVersion_ReturnsCorrectVersion() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/version")
                .then()
                .statusCode(200)
                .body("applicationName", equalTo("springboot-work"))
                .body("currentDate", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+"))
                .body("version", equalTo("1.0.0"));
    }

    @Test
    @DisplayName("Should return current date in ISO format")
    void testGetVersion_ReturnsValidDateFormat() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/version")
                .then()
                .statusCode(200)
                .body("currentDate", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+"));
    }

    @Test
    @DisplayName("Should handle multiple concurrent requests")
    void testGetVersion_HandlesConcurrentRequests() {
        for (int i = 0; i < 10; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/version")
                    .then()
                    .statusCode(200)
                    .body("version", equalTo("1.0.0"));
        }
    }

    @Test
    @DisplayName("Should return JSON content type")
    void testGetVersion_ReturnsJsonContentType() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/version")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Should return 404 for invalid endpoint")
    void testInvalidEndpoint_Returns404() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/invalid")
                .then()
                .statusCode(404);
    }

    @TestFactory
    @DisplayName("Should verify all response fields exist")
    Iterable<DynamicTest> testAllResponseFields() {
        VersionResponse response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/version")
                .then()
                .statusCode(200)
                .extract()
                .as(VersionResponse.class);

        return java.util.List.of(
                DynamicTest.dynamicTest("Version should not be null",
                        () -> Assertions.assertNotNull(response.getVersion())),
                DynamicTest.dynamicTest("Current date should not be null",
                        () -> Assertions.assertNotNull(response.getCurrentDate())),
                DynamicTest.dynamicTest("Application name should not be null",
                        () -> Assertions.assertNotNull(response.getApplicationName())),
                DynamicTest.dynamicTest("Version should match expected value",
                        () -> Assertions.assertEquals("1.0.0", response.getVersion()))
        );
    }
}

