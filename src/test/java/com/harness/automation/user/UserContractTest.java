package com.harness.automation.user;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service — Provider Contract Tests")
class UserContractTest extends BaseApiTest {

    @Value("${services.user.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-US-C01: GET /api/users/email/{email} — Response must have top-level 'collection' field")
    void findByEmail_responseSchema_shouldHaveCollectionField() {
        givenJson(baseUrl)
                .when()
                .get("/api/users/email/{email}", "test@example.com")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("$", hasKey("collection"));
    }

    @Test
    @Order(2)
    @DisplayName("TC-US-C02: GET /api/users/email/{email} — UserDto in collection must have required fields")
    void findByEmail_userDtoSchema_shouldHaveRequiredFields() {
        givenJson(baseUrl)
                .when()
                .get("/api/users/email/{email}", "testuser@automation.com")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.findAll { it }.every { it.containsKey('userId') }", is(true))
                .body("collection.findAll { it }.every { it.containsKey('firstName') }", is(true))
                .body("collection.findAll { it }.every { it.containsKey('email') }", is(true));
    }

    @Test
    @Order(3)
    @DisplayName("TC-US-C03: GET /api/users — DtoCollectionResponse schema regression check")
    void getAllUsers_dtoCollectionResponseSchema_shouldHaveCollectionField() {
        givenJson(baseUrl)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("$", hasKey("collection"))
                .body("collection", is(notNullValue()))
                .body("collection", instanceOf(java.util.List.class));
    }
}
