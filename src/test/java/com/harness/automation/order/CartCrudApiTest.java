package com.harness.automation.order;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Order Service — Cart CRUD API Tests")
class CartCrudApiTest extends BaseApiTest {

    @Value("${services.order.base-url}")
    private String baseUrl;

    private static Integer createdCartId;

    @Test
    @Order(1)
    @DisplayName("POST /api/carts — Create cart should return 200 with CartDto")
    void createCart_shouldReturn200WithCartDto() {
        String body = """
                {
                    "userId": 1
                }
                """;

        createdCartId = givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/carts")
                .then()
                .statusCode(200)
                .extract()
                .path("cartId");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/carts — Get all carts should return cart list")
    void getAllCarts_shouldReturnCartList() {
        givenJson(baseUrl)
                .when()
                .get("/api/carts")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/carts/{cartId} — Get cart by ID should return CartDto")
    void getCartById_shouldReturnCartDto() {
        Assumptions.assumeTrue(createdCartId != null, "Cart must be created first");

        givenJson(baseUrl)
                .when()
                .get("/api/carts/{cartId}", createdCartId)
                .then()
                .statusCode(200)
                .body("cartId", equalTo(createdCartId));
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /api/carts/{cartId} — Delete cart should return 200")
    void deleteCartById_shouldReturn200() {
        Assumptions.assumeTrue(createdCartId != null, "Cart must be created first");

        givenJson(baseUrl)
                .when()
                .delete("/api/carts/{cartId}", createdCartId)
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

}
