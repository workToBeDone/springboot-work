package com.techlearning.dto;

import java.time.LocalDateTime;

public class VersionResponse {
    private String version;
    private LocalDateTime currentDate;
    private String applicationName;

    public VersionResponse() {}

    public VersionResponse(String applicationVersion, LocalDateTime now, String applicationName) {
        this.version = applicationVersion;
        this.currentDate = now;
        this.applicationName = applicationName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDateTime currentDate) {
        this.currentDate = currentDate;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
