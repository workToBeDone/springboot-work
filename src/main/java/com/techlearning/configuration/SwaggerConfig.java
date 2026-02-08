package com.techlearning.configuration;

import com.techlearning.config.ApplicationConfigProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    //@OpenAPIDefinition(info = @Info(title = "Demo API", version = "2.0", description = "Swagger API Demo"))

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    OpenAPI customOpenAPI(ApplicationConfigProperties applicationConfigProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo App")
                        .version("1.0")
                        .contact(new Contact().email("").name("DemoApp"))
                        .description("Demo Application API documentation"))
                .servers(List.of((new Server().url(applicationConfigProperties.openapiDevUrl()).description("Development environment URL"))));
    }
}