package com.techlearning.controller;

import com.techlearning.dto.VersionResponse;
import com.techlearning.service.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class VersionController {

    Logger logger = LoggerFactory.getLogger(VersionController.class);

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping("/version")
    public ResponseEntity<VersionResponse> getVersion() {
        return ResponseEntity.ok(versionService.getVersionInfo());
    }
}
