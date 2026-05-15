package com.harness.automation.user;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service — Email Search API Tests")
class UserEmailSearchApiTest extends BaseApiTest {

    @Value("${services.user.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-US-E01: GET /api/users/email/{email} — Valid email should return 200 with DtoCollectionResponse")
    void findByEmail_validEmail_shouldReturn200WithCollection() {
        givenJson(baseUrl)
                .when()
                .get("/api/users/email/{email}", "testuser@automation.com")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(2)
    @DisplayName("TC-US-E02: GET /api/users/email/{email} — Unknown email should return 200 with collection")
    void findByEmail_unknownEmail_shouldReturn200WithCollection() {
        givenJson(baseUrl)
                .when()
                .get("/api/users/email/{email}", "nobody@nonexistent.invalid")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("TC-US-E03: GET /api/users/email/ — Missing email path segment should return 4xx")
    void findByEmail_missingEmail_shouldReturn4xx() {
        givenJson(baseUrl)
                .when()
                .get("/api/users/email/")
                .then()
                .statusCode(anyOf(is(400), is(404), is(405)));
    }
}
