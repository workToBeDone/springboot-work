package com.techlearning.integration;

import com.techlearning.domains.Order;
import com.techlearning.repository.OrderRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {
    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withExposedPorts(5432)
            //.withInitScript("import.sql")  // Load init script
            .withReuse(false);  // Always start fresh

    @Autowired
    OrderRepository orderRepository;

    private RestTestClient restTestClient;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

//    @BeforeAll
//    static void beforeAll() {
//        postgres.start();
//    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> "jdbc:postgresql://" + postgres.getHost() + ":" +
                        postgres.getMappedPort(5432) + "/testdb");
        registry.add("spring.datasource.username", () -> "testuser");
        registry.add("spring.datasource.password", () -> "testpass");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @BeforeAll
    static void initializeDatabase() throws SQLException {
        postgres.start();
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + postgres.getHost() + ":" +
                        postgres.getMappedPort(5432) + "/testdb",
                "testuser",
                "testpass");
             Statement statement = connection.createStatement()) {

            // Verify PostgreSQL connection
            statement.execute("SELECT version()");
        }
    }

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        RestAssured.baseURI = "http://localhost:" + port;

        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");

        runSqlScriptFromFile("import.sql");
        jdbcTemplate.update("INSERT INTO orders (description) VALUES ('Init Order 1');");
        jdbcTemplate.update("INSERT INTO orders (description) VALUES ('Init Order 2');");
        //orderRepository.deleteAll();
        this.restTestClient = RestTestClient
                .bindToApplicationContext(context)
                .build();
    }

    public void runSqlScriptFromFile(String scriptPath) {
        // 1. Specify the SQL file location as a Spring Resource
        Resource resource = new ClassPathResource(scriptPath);

        // 2. Create and configure the populator
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        // Optional: configure delimiters, comment handling, etc.
        databasePopulator.setSeparator(";");
        databasePopulator.setIgnoreFailedDrops(true); // Ignore errors when dropping tables, etc.

        // 3. Execute the script using the DataSource
        databasePopulator.execute(dataSource);

        System.out.println("SQL script executed successfully from file: " + scriptPath);
    }

    @Test
    @DisplayName("Should create new Order")
    void test_createOrder() {
        Order order = new Order();
        order.setDescription("Order 1");
        restTestClient.post()
                .uri("/orders/createOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .body(order)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Order.class)
                .value(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getDescription()).isEqualTo("Order 1");
                })
        ;

    }

    @Test
    void test_OrderList() {
        /*Order order = new Order();
        order.setDescription("Order 1");

        orderRepository.save(order);*/

        restTestClient.get()
                .uri("/orders/allOrders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4)
                .jsonPath("$[0].description").isEqualTo("Init Order 1");
    }

    /*@Test
    void test_OrderList() {
        restTestClient.get()
                .uri("/orders/allOrders")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class)
                .value(orders -> {
                    assertThat(orders).hasSize(0);
                });
    }*/

    /*@Test
    void test_OrderList() {
        Order order = new Order();
        order.setDescription("Order 1");

        orderRepository.save(order);

        given().contentType(ContentType.JSON)
                .when().get("/orders/allOrders")
                .then().statusCode(200)
                .body(".", hasSize(1));
    }*/

/*@Test
    void test_createOrder() {
        Order order = new Order();
        order.setDescription("Order 1");

        given().contentType(ContentType.JSON)
                .when().post("/orders/createOrder").contentType().
    }*/
}