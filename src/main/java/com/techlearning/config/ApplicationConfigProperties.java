package com.techlearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.config")
public record ApplicationConfigProperties(String openapiDevUrl, String remoteServiceUrl) {
}
