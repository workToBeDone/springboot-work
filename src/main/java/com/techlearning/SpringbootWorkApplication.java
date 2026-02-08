package com.techlearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.techlearning.config")
public class SpringbootWorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWorkApplication.class, args);
    }

}
