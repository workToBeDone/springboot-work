package com.techlearning.controller;

import com.techlearning.dto.VersionResponse;
import com.techlearning.service.VersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VersionController Unit Tests")
class VersionControllerTest {

    @Mock
    private VersionService versionService;

    @InjectMocks
    private VersionController versionController;

    private VersionResponse mockVersionResponse;

    @BeforeEach
    void setUp() {
        mockVersionResponse = new VersionResponse(
                "1.0.0",
                LocalDateTime.now(),
                "version-api"
        );
    }

    @Test
    @DisplayName("Should return 200 OK status")
    void testGetVersion_ReturnsOkStatus() {
        // Arrange
        when(versionService.getVersionInfo()).thenReturn(mockVersionResponse);

        // Act
        ResponseEntity<VersionResponse> response = versionController.getVersion();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return version response from service")
    void testGetVersion_ReturnsVersionResponse() {
        // Arrange
        when(versionService.getVersionInfo()).thenReturn(mockVersionResponse);

        // Act
        ResponseEntity<VersionResponse> response = versionController.getVersion();

        // Assert
        assertNotNull(response.getBody());
        assertEquals("1.0.0", response.getBody().getVersion());
        assertEquals("version-api", response.getBody().getApplicationName());
    }

    @Test
    @DisplayName("Should call version service once")
    void testGetVersion_CallsServiceOnce() {
        // Arrange
        when(versionService.getVersionInfo()).thenReturn(mockVersionResponse);

        // Act
        versionController.getVersion();

        // Assert
        verify(versionService, times(1)).getVersionInfo();
    }
}