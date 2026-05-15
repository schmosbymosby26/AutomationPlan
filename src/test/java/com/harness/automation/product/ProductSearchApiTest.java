package com.harness.automation.product;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product Service — Product Search API Tests")
class ProductSearchApiTest extends BaseApiTest {

    @Value("${services.product.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("TC-SEARCH-001: GET /api/products/search/{keyword} — valid keyword returns 200 with DtoCollectionResponse")
    void searchByKeyword_withValidKeyword_shouldReturn200WithCollection() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/search/{keyword}", "test")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(2)
    @DisplayName("TC-SEARCH-002: GET /api/products/search/{keyword} — keyword matching products returns collection with size >= 0")
    void searchByKeyword_withMatchingKeyword_shouldReturnCollection() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/search/{keyword}", "Product")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(3)
    @DisplayName("TC-SEARCH-003: GET /api/products/search/{keyword} — blank/whitespace keyword triggers client error")
    void searchByKeyword_withBlankKeyword_shouldReturnClientError() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/search/ ")
                .then()
                .statusCode(anyOf(is(400), is(404), is(405)));
    }

    @Test
    @Order(4)
    @DisplayName("TC-SEARCH-006: GET /api/products/search/{keyword} — non-matching keyword still returns 200 with collection")
    void searchByKeyword_withNonMatchingKeyword_shouldReturn200WithCollection() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/search/{keyword}", "zzznomatchkeyword9999")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

}
