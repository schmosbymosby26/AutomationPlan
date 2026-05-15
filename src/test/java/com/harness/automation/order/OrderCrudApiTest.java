package com.harness.automation.order;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Order Service — CRUD API Tests")
class OrderCrudApiTest extends BaseApiTest {

    @Value("${services.order.base-url}")
    private String baseUrl;

    private static Integer createdOrderId;

    @Test
    @Order(1)
    @DisplayName("POST /api/orders — Create order should return 200 with OrderDto")
    void createOrder_shouldReturn200WithOrderDto() {
        String body = """
                {
                    "orderDate": "2026-05-15 10:00:00",
                    "orderDesc": "Test order for automation",
                    "orderFee": 99.99,
                    "cart": {
                        "cartId": 1
                    }
                }
                """;

        createdOrderId = givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("orderDesc", equalTo("Test order for automation"))
                .body("orderFee", equalTo(99.99f))
                .extract()
                .path("orderId");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/orders — Get all orders should return order list")
    void getAllOrders_shouldReturnOrderList() {
        givenJson(baseUrl)
                .when()
                .get("/api/orders")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/orders/{orderId} — Get order by ID should return OrderDto")
    void getOrderById_shouldReturnOrderDto() {
        Assumptions.assumeTrue(createdOrderId != null, "Order must be created first");

        givenJson(baseUrl)
                .when()
                .get("/api/orders/{orderId}", createdOrderId)
                .then()
                .statusCode(200)
                .body("orderId", equalTo(createdOrderId))
                .body("orderDesc", is(notNullValue()));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/orders/{orderId} — Update order should return updated OrderDto")
    void updateOrder_shouldReturnUpdatedOrderDto() {
        Assumptions.assumeTrue(createdOrderId != null, "Order must be created first");

        String body = String.format("""
                {
                    "orderId": %d,
                    "orderDate": "2026-05-15 12:00:00",
                    "orderDesc": "Updated test order",
                    "orderFee": 149.99,
                    "cart": {
                        "cartId": 1
                    }
                }
                """, createdOrderId);

        givenJson(baseUrl)
                .body(body)
                .when()
                .put("/api/orders/{orderId}", createdOrderId)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/orders/{orderId} — Delete order should return 200")
    void deleteOrderById_shouldReturn200() {
        Assumptions.assumeTrue(createdOrderId != null, "Order must be created first");

        givenJson(baseUrl)
                .when()
                .delete("/api/orders/{orderId}", createdOrderId)
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/orders/{orderId} — Get non-existent order should return 404")
    void getOrderById_withInvalidId_shouldReturn404() {
        givenJson(baseUrl)
                .when()
                .get("/api/orders/{orderId}", 999999)
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }

}
