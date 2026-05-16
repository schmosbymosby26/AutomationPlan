package com.harness.automation.shipping;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Shipping Service — Track by Order ID API Tests (PR #18)")
class ShippingTrackingApiTest extends BaseApiTest {

    @Value("${services.shipping.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-SHP-TRACK-001: GET /api/shippings/track/{orderId} — valid orderId should return 200")
    void trackByOrderId_validOrderId_shouldReturn200() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", 1)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("TC-SHP-TRACK-002: GET /api/shippings/track/{orderId} — response wraps items in collection field")
    void trackByOrderId_validOrderId_shouldReturnCollectionWrapper() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", 1)
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("TC-SHP-TRACK-003: GET /api/shippings/track/{orderId} — different orderId values each return 200")
    void trackByOrderId_differentOrderIds_shouldReturn200() {
        for (int orderId : new int[]{1, 2, 99}) {
            givenJson(baseUrl)
                    .when()
                    .get("/api/shippings/track/{orderId}", orderId)
                    .then()
                    .statusCode(200)
                    .body("collection", is(notNullValue()));
        }
    }
}
