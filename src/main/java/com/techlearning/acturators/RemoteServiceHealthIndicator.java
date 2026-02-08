package com.techlearning.acturators;

import com.techlearning.config.ApplicationConfigProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Component
public class RemoteServiceHealthIndicator implements HealthIndicator {

//    @Override
//    public @Nullable Health health(boolean includeDetails) {
//        return HealthIndicator.super.health(includeDetails);
//    }

    private final RestTemplate restTemplate = new RestTemplate();
    ApplicationConfigProperties applicationConfigProperties;
    //private static final String REMOTE_SERVICE_URL = "https://www.yahoo.com/";

    RemoteServiceHealthIndicator(ApplicationConfigProperties applicationConfigProperties) {
        this.applicationConfigProperties = applicationConfigProperties;
    }

    @Override
    public Health health() {
        String externalAllURL = applicationConfigProperties.remoteServiceUrl();
        if (externalAllURL == null || externalAllURL.isEmpty()) {
            return Health.unknown().withDetail("externalAllURL", "No URL configured").build();
        }
        try {

            HttpStatusCode statusCode = RestClient.create(externalAllURL).method(HttpMethod.HEAD)
                    .exchange((req, resp) -> resp.getStatusCode());

            if (HttpStatus.OK.equals(statusCode)) {
                return Health.up().withDetail("RemoteService", "Available").build();
            } else if (HttpStatus.BAD_REQUEST.equals(statusCode)) {
                return Health.down().withDetail("BAD_REQUEST", "Error or Unavailable").build();
            } else {
                return Health.down().withDetail("NOT_FOUND", "Error or Unavailable").build();
            }
        } catch (Exception e) {
            return Health.down(e).withDetail("RemoteService", "Error or Unavailable").build();
        }
    }
}
