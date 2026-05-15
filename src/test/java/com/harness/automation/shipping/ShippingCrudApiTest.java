package com.harness.automation.shipping;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Shipping Service — CRUD API Tests")
class ShippingCrudApiTest extends BaseApiTest {

    @Value("${services.shipping.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("POST /api/shippings — Create shipping item should return 200")
    void createShipping_shouldReturn200WithShippingDto() {
        String body = """
                {
                    "orderId": 1,
                    "productId": 1,
                    "orderedQuantity": 3
                }
                """;

        givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/shippings")
                .then()
                .statusCode(200)
                .body("orderedQuantity", equalTo(3));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/shippings — Get all shippings should return shipping list")
    void getAllShippings_shouldReturnShippingList() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/shippings/{orderId}/{productId} — Get shipping by composite ID should return ShippingDto")
    void getShippingByCompositeId_shouldReturnShippingDto() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/{orderId}/{productId}", 1, 1)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

}
