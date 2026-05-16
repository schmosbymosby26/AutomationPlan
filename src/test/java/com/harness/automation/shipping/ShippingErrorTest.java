package com.harness.automation.shipping;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Shipping Service — Error Scenario Tests (PR #18)")
class ShippingErrorTest extends BaseApiTest {

    @Value("${services.shipping.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-SHP-ERROR-001: GET /api/shippings/track/{orderId} — non-numeric orderId should return 4xx or 200 (implementation-defined)")
    void trackByOrderId_nonNumericOrderId_shouldHandleGracefully() {
        // The current implementation passes orderId as String and calls findAll(),
        // so non-numeric values may still return 200. We assert the response is not a 5xx.
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", "abc")
                .then()
                .statusCode(not(greaterThanOrEqualTo(500)));
    }

    @Test
    @Order(2)
    @DisplayName("TC-SHP-ERROR-002: GET /api/shippings/track — missing path variable should return 404 or 405")
    void trackByOrderId_missingPathVariable_shouldReturn404Or405() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track")
                .then()
                .statusCode(anyOf(is(404), is(405)));
    }

    @Test
    @Order(3)
    @DisplayName("TC-SHP-ERROR-003: GET /api/shippings/track/{orderId} — very large orderId should not cause 5xx")
    void trackByOrderId_veryLargeOrderId_shouldHandleGracefully() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", Long.MAX_VALUE)
                .then()
                .statusCode(not(greaterThanOrEqualTo(500)));
    }
}
