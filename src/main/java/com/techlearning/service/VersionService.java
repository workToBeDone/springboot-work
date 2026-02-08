package com.techlearning.service;

import com.techlearning.dto.VersionResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VersionService {

    @Value("${application.version:1.0.0}")
    private String applicationVersion;

    @Value("${spring.application.name:version-api}")
    private String applicationName;

    public VersionResponse getVersionInfo() {
        return new VersionResponse(
                applicationVersion,
                LocalDateTime.now(),
                applicationName
        );
    }
}
