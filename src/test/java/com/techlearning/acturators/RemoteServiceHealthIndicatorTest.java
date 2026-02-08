package com.techlearning.acturators;

import com.techlearning.config.ApplicationConfigProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class RemoteServiceHealthIndicatorTest {

    @Autowired
    private RemoteServiceHealthIndicator remoteServiceHealthIndicator;

    @MockitoBean
    private ApplicationConfigProperties applicationConfigProperties;

    @Test
    @DisplayName("Status UNKNOWN :: When External application URL is null")
    public void test_Service_Unknow() {
        Health health = remoteServiceHealthIndicator.health();
        Assertions.assertAll(
                () -> assertEquals("UNKNOWN", health.getStatus().getCode())
        );
    }

    @Test
    @DisplayName("Status UP :: When application URL is available and status code 200")
    public void test_Service_Up_200() {
        when(applicationConfigProperties.remoteServiceUrl()).thenReturn("https://aws-api.416.io/sc/v1/200");


        Health health = remoteServiceHealthIndicator.health();
        Assertions.assertAll(
                () -> assertEquals("UP", health.getStatus().getCode()),
                () -> assertEquals("Available", health.getDetails().get("RemoteService"))
        );
    }

    @Test
    @DisplayName("Status DOWN (400) :: When application URL with status code 400")
    public void test_Service_Down_400() {
        when(applicationConfigProperties.remoteServiceUrl()).thenReturn("https://aws-api.416.io/sc/v1/400");

        Health health = remoteServiceHealthIndicator.health();
        Assertions.assertAll(
                () -> assertEquals("DOWN", health.getStatus().getCode()),
                () -> assertEquals("Error or Unavailable", health.getDetails().get("BAD_REQUEST"))
        );
    }

    @Test
    @DisplayName("Status DOWN (404) :: When application URL with status code 404")
    public void test_Service_Down_404() {
        when(applicationConfigProperties.remoteServiceUrl()).thenReturn("https://aws-api.416.io/sc/v1/404");

        Health health = remoteServiceHealthIndicator.health();
        Assertions.assertAll(
                () -> assertEquals("DOWN", health.getStatus().getCode()),
                () -> assertEquals("Error or Unavailable", health.getDetails().get("NOT_FOUND"))
        );
    }

}