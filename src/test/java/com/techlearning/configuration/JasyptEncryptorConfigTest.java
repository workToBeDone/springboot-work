package com.techlearning.configuration;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JasyptEncryptorConfigTest {

    private static final String PROPERTY_KEY = "app.config.jasypt_encryptor";

    @Mock
    private Environment environment;

    @InjectMocks
    private JasyptEncryptorConfig config;

    @Test
    @DisplayName("stringEncryptor() returns configured StandardPBEStringEncryptor when property present and non-blank")
    void stringEncryptor_happyPath() {
        String expectedPassword = "mySecretPassword";
        when(environment.getProperty(PROPERTY_KEY)).thenReturn(expectedPassword);

        StringEncryptor encryptor = config.stringEncryptor();

        assertNotNull(encryptor);
        assertTrue(encryptor instanceof StandardPBEStringEncryptor);

        // indirect verification: encrypt/decrypt roundtrip to ensure password is actually set
        StandardPBEStringEncryptor standard = (StandardPBEStringEncryptor) encryptor;
        String plain = "test-value";
        String encrypted = standard.encrypt(plain);
        assertNotNull(encrypted);
        assertNotEquals(plain, encrypted);
        String decrypted = standard.decrypt(encrypted);
        assertEquals(plain, decrypted);

        verify(environment).getProperty(PROPERTY_KEY);
        verifyNoMoreInteractions(environment);
    }

    @Nested
    @DisplayName("stringEncryptor() error scenarios")
    class ErrorScenarios {

        @Test
        @DisplayName("throws IllegalStateException when property is null")
        void stringEncryptor_nullProperty() {
            when(environment.getProperty(PROPERTY_KEY)).thenReturn(null);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> config.stringEncryptor()
            );

            assertEquals("Missing 'JASYPT_ENCRYPTOR_PASSWORD' environment variable", ex.getMessage());
            verify(environment).getProperty(PROPERTY_KEY);
        }

        @Test
        @DisplayName("throws IllegalStateException when property is empty string")
        void stringEncryptor_emptyProperty() {
            when(environment.getProperty(PROPERTY_KEY)).thenReturn("");

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> config.stringEncryptor()
            );

            assertEquals("Missing 'JASYPT_ENCRYPTOR_PASSWORD' environment variable", ex.getMessage());
            verify(environment).getProperty(PROPERTY_KEY);
        }

        @Test
        @DisplayName("throws IllegalStateException when property is blank spaces")
        void stringEncryptor_blankProperty() {
            when(environment.getProperty(PROPERTY_KEY)).thenReturn("   ");

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> config.stringEncryptor()
            );

            assertEquals("Missing 'JASYPT_ENCRYPTOR_PASSWORD' environment variable", ex.getMessage());
            verify(environment).getProperty(PROPERTY_KEY);
        }
    }
}
