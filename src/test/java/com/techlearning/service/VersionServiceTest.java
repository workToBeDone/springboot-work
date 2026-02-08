package com.techlearning.service;

import com.techlearning.dto.VersionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VersionService Unit Tests")
class VersionServiceTest {

    private VersionService versionService;

    @BeforeEach
    void setUp() {
        versionService = new VersionService();
        ReflectionTestUtils.setField(versionService, "applicationVersion", "1.0.0");
        ReflectionTestUtils.setField(versionService, "applicationName", "version-api");
    }

    @Test
    @DisplayName("Should return version info with correct application version")
    void testGetVersionInfo_ReturnsCorrectVersion() {
        // Act
        VersionResponse response = versionService.getVersionInfo();

        // Assert
        assertNotNull(response);
        assertEquals("1.0.0", response.getVersion());
        assertEquals("version-api", response.getApplicationName());
    }

    @Test
    @DisplayName("Should return current date time")
    void testGetVersionInfo_ReturnsCurrentDateTime() {
        // Arrange
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        VersionResponse response = versionService.getVersionInfo();

        // Assert
        LocalDateTime afterCall = LocalDateTime.now();
        assertNotNull(response.getCurrentDate());
        assertTrue(response.getCurrentDate().isAfter(beforeCall.minusSeconds(1)));
        assertTrue(response.getCurrentDate().isBefore(afterCall.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should return non-null response")
    void testGetVersionInfo_ReturnsNonNullResponse() {
        // Act
        VersionResponse response = versionService.getVersionInfo();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getVersion());
        assertNotNull(response.getCurrentDate());
        assertNotNull(response.getApplicationName());
    }
}