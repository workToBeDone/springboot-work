package com.techlearning.acturators;

import com.techlearning.config.ApplicationConfigProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RemoteServiceHealthIndicatorIntTest {

    @Autowired
    private RemoteServiceHealthIndicator remoteServiceHealthIndicator;

    @Autowired
    private ApplicationConfigProperties applicationConfigProperties;

    @Test
    @DisplayName("Status UP :: When application URL is available and status code 200")
    public void test_Service_Up_200() {
        Health health = remoteServiceHealthIndicator.health();
        Assertions.assertAll(
                () -> assertEquals("UP", health.getStatus().getCode()),
                () -> assertEquals("Available", health.getDetails().get("RemoteService"))
        );
    }
}