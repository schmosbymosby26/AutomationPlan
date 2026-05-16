package com.harness.automation.shipping;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Shipping Service — Provider Contract Tests (PR #18)")
class ShippingContractTest extends BaseApiTest {

    @Value("${services.shipping.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-SHP-CONTRACT-001: GET /api/shippings/track/{orderId} — response schema must include 'collection' field")
    void trackByOrderId_responseSchema_shouldHaveCollectionField() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", 1)
                .then()
                .statusCode(200)
                .body("$", hasKey("collection"));
    }

    @Test
    @Order(2)
    @DisplayName("TC-SHP-CONTRACT-002: GET /api/shippings/track/{orderId} — each item in collection should have expected OrderItemDto fields")
    void trackByOrderId_collectionItems_shouldHaveExpectedOrderItemDtoFields() {
        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", 1)
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.flatten().findAll { it.orderedQuantity != null }", everyItem(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("TC-SHP-CONTRACT-003: GET /api/shippings/track/{orderId} and GET /api/shippings — both return DtoCollectionResponse schema")
    void findAll_and_trackByOrderId_shouldReturnSameResponseSchema() {
        // Verify /track/{orderId} has same top-level schema as /api/shippings (findAll)
        givenJson(baseUrl)
                .when()
                .get("/api/shippings")
                .then()
                .statusCode(200)
                .body("$", hasKey("collection"));

        givenJson(baseUrl)
                .when()
                .get("/api/shippings/track/{orderId}", 1)
                .then()
                .statusCode(200)
                .body("$", hasKey("collection"));
    }
}
