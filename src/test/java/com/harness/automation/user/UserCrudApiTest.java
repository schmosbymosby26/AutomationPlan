package com.harness.automation.user;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service — User CRUD API Tests")
class UserCrudApiTest extends BaseApiTest {

    @Value("${services.user.base-url}")
    private String baseUrl;

    private static Integer createdUserId;

    @Test
    @Order(1)
    @DisplayName("POST /api/users — Create user should return 200 with UserDto")
    void createUser_shouldReturn200WithUserDto() {
        String body = """
                {
                    "firstName": "Test",
                    "lastName": "User",
                    "imageUrl": "https://example.com/avatar.png",
                    "email": "testuser@automation.com",
                    "phone": "1234567890",
                    "credential": {
                        "username": "testuser_auto",
                        "password": "securePass123",
                        "roleBasedAuthority": "ROLE_USER",
                        "isEnabled": true,
                        "isAccountNonExpired": true,
                        "isAccountNonLocked": true,
                        "isCredentialsNonExpired": true
                    }
                }
                """;

        createdUserId = givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Test"))
                .body("lastName", equalTo("User"))
                .extract()
                .path("userId");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/users — Get all users should return user list")
    void getAllUsers_shouldReturnUserList() {
        givenJson(baseUrl)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/users/{userId} — Get user by ID should return UserDto")
    void getUserById_shouldReturnUserDto() {
        Assumptions.assumeTrue(createdUserId != null, "User must be created first");

        givenJson(baseUrl)
                .when()
                .get("/api/users/{userId}", createdUserId)
                .then()
                .statusCode(200)
                .body("userId", equalTo(createdUserId))
                .body("firstName", equalTo("Test"));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/users/username/{username} — Get user by username should return UserDto")
    void getUserByUsername_shouldReturnUserDto() {
        givenJson(baseUrl)
                .when()
                .get("/api/users/username/{username}", "testuser_auto")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Test"));
    }

    @Test
    @Order(5)
    @DisplayName("PUT /api/users/{userId} — Update user should return updated UserDto")
    void updateUser_shouldReturnUpdatedUserDto() {
        Assumptions.assumeTrue(createdUserId != null, "User must be created first");

        String body = String.format("""
                {
                    "userId": %d,
                    "firstName": "Updated",
                    "lastName": "User",
                    "imageUrl": "https://example.com/avatar2.png",
                    "email": "updated@automation.com",
                    "phone": "9876543210"
                }
                """, createdUserId);

        givenJson(baseUrl)
                .body(body)
                .when()
                .put("/api/users/{userId}", createdUserId)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(6)
    @DisplayName("DELETE /api/users/{userId} — Delete user should return 200")
    void deleteUserById_shouldReturn200() {
        Assumptions.assumeTrue(createdUserId != null, "User must be created first");

        givenJson(baseUrl)
                .when()
                .delete("/api/users/{userId}", createdUserId)
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

}
