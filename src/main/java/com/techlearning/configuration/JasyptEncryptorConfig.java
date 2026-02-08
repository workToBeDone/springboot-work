package com.techlearning.configuration;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class JasyptEncryptorConfig {

    private final Environment environment;

    public JasyptEncryptorConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        String encryptorPWD = environment.getProperty("app.config.jasypt_encryptor");
        if (encryptorPWD == null || encryptorPWD.isBlank()) {
            throw new IllegalStateException("Missing 'JASYPT_ENCRYPTOR_PASSWORD' environment variable");
        }

        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setPassword(encryptorPWD);
        return standardPBEStringEncryptor;
    }
}
