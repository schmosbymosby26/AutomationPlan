package com.harness.automation.product;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product Service — Product Search Contract Tests")
class ProductSearchContractTest extends BaseApiTest {

    @Value("${services.product.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-SEARCH-004: GET /api/products/search/{keyword} — provider contract: response must contain top-level 'collection' field")
    void searchByKeyword_providerContract_responseMustHaveCollectionField() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/search/{keyword}", "test")
                .then()
                .statusCode(200)
                .body("$", hasKey("collection"));
    }

    @Test
    @Order(2)
    @DisplayName("TC-SEARCH-005: GET /api/products/search/{keyword} — provider contract: each product in collection has required fields")
    void searchByKeyword_providerContract_eachProductHasRequiredFields() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/search/{keyword}", "test")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.findAll { it }.every { p -> p.containsKey('productId') }", is(true))
                .body("collection.findAll { it }.every { p -> p.containsKey('productTitle') }", is(true))
                .body("collection.findAll { it }.every { p -> p.containsKey('sku') }", is(true))
                .body("collection.findAll { it }.every { p -> p.containsKey('priceUnit') }", is(true));
    }

}
