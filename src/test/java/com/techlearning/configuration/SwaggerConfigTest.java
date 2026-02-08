package com.techlearning.configuration;

import com.techlearning.config.ApplicationConfigProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "application.settings.jasypt-encryptor=testPassword123",
        "application.settings.openapi-dev-url=http://localhost:8080"
})
class SwaggerConfigTest {

    @Autowired
    private SwaggerConfig swaggerConfig;

    @Autowired
    private GroupedOpenApi publicApi;

    @Autowired
    private OpenAPI customOpenAPI;

    @Autowired
    private ApplicationConfigProperties applicationConfigProperties;

    @Test
    void testPublicApiBeanCreation() {
        assertAll(
                () -> assertNotNull(publicApi),
                () -> assertNotNull(publicApi.getGroup()),
                () -> assertEquals("public-apis", publicApi.getGroup()),
                () -> assertNotNull(publicApi.getPathsToMatch()),
                () -> assertFalse(publicApi.getPathsToMatch().isEmpty())
        );
    }

    @Test
    void testPublicApiDirectInvocation() {
        SwaggerConfig config = new SwaggerConfig();

        GroupedOpenApi groupedOpenApi = config.publicApi();

        assertAll(
                () -> assertNotNull(groupedOpenApi),
                () -> assertEquals("public-apis", groupedOpenApi.getGroup()),
                () -> assertNotNull(groupedOpenApi.getPathsToMatch()),
                () -> assertFalse(groupedOpenApi.getPathsToMatch().isEmpty()),
                () -> assertTrue(groupedOpenApi.getPathsToMatch().contains("/**"))
        );
    }

    @Test
    void testCustomOpenAPIBeanCreation() {
        assertNotNull(customOpenAPI);

        Info info = customOpenAPI.getInfo();
        assertAll("OpenAPI Info",
                () -> assertEquals("Demo App", info.getTitle()),
                () -> assertEquals("1.0", info.getVersion()),
                () -> assertEquals("Demo Application API documentation", info.getDescription())
        );
    }

    @Test
    void testCustomOpenAPIContact() {
        Contact contact = customOpenAPI.getInfo().getContact();
        assertAll("Contact Info",
                () -> assertNotNull(contact),
                () -> assertEquals("DemoApp", contact.getName()),
                () -> assertEquals("", contact.getEmail())
        );
    }

    @Test
    void testCustomOpenAPIServers() {
        List<Server> servers = customOpenAPI.getServers();
        assertNotNull(servers);
        assertFalse(servers.isEmpty());

        Server server = servers.get(0);
        assertAll("Server Configuration",
                () -> assertEquals("http://localhost:8080", server.getUrl()),
                () -> assertEquals("Development environment URL", server.getDescription())
        );
    }

    @Test
    void testSwaggerConfigBeanIsInjected() {
        assertNotNull(swaggerConfig);
    }

    @Test
    void testApplicationConfigPropertiesInjection() {
        assertNotNull(applicationConfigProperties);
        assertEquals("http://localhost:8080", applicationConfigProperties.openapiDevUrl());
    }
}
