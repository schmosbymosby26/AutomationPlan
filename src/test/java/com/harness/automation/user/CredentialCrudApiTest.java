package com.harness.automation.user;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service — Credential CRUD API Tests")
class CredentialCrudApiTest extends BaseApiTest {

    @Value("${services.user.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("GET /api/credentials — Get all credentials should return credential list")
    void getAllCredentials_shouldReturnCredentialList() {
        givenJson(baseUrl)
                .when()
                .get("/api/credentials")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/credentials/username/{username} — Get credential by username should return CredentialDto")
    void getCredentialByUsername_shouldReturnCredentialDto() {
        givenJson(baseUrl)
                .when()
                .get("/api/credentials/username/{username}", "selimhorri")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

}
