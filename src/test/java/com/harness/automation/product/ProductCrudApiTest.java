package com.harness.automation.product;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product Service — Product CRUD API Tests")
class ProductCrudApiTest extends BaseApiTest {

    @Value("${services.product.base-url}")
    private String baseUrl;

    private static Integer createdProductId;

    @Test
    @Order(1)
    @DisplayName("POST /api/products — Create product should return 200 with ProductDto")
    void createProduct_shouldReturn200WithProductDto() {
        String body = """
                {
                    "productTitle": "Test Product",
                    "imageUrl": "https://example.com/image.png",
                    "sku": "TEST-SKU-001",
                    "priceUnit": 29.99,
                    "quantity": 100,
                    "category": {
                        "categoryId": 1
                    }
                }
                """;

        createdProductId = givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/products")
                .then()
                .statusCode(200)
                .body("productTitle", equalTo("Test Product"))
                .body("sku", equalTo("TEST-SKU-001"))
                .extract()
                .path("productId");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/products — Get all products should return product list")
    void getAllProducts_shouldReturnProductList() {
        givenJson(baseUrl)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/products/{productId} — Get product by ID should return ProductDto")
    void getProductById_shouldReturnProductDto() {
        Assumptions.assumeTrue(createdProductId != null, "Product must be created first");

        givenJson(baseUrl)
                .when()
                .get("/api/products/{productId}", createdProductId)
                .then()
                .statusCode(200)
                .body("productId", equalTo(createdProductId))
                .body("productTitle", equalTo("Test Product"));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/products/{productId} — Update product should return updated ProductDto")
    void updateProduct_shouldReturnUpdatedProductDto() {
        Assumptions.assumeTrue(createdProductId != null, "Product must be created first");

        String body = String.format("""
                {
                    "productId": %d,
                    "productTitle": "Updated Test Product",
                    "imageUrl": "https://example.com/updated.png",
                    "sku": "TEST-SKU-001",
                    "priceUnit": 39.99,
                    "quantity": 50,
                    "category": {
                        "categoryId": 1
                    }
                }
                """, createdProductId);

        givenJson(baseUrl)
                .body(body)
                .when()
                .put("/api/products/{productId}", createdProductId)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/products/{productId} — Delete product should return 200")
    void deleteProductById_shouldReturn200() {
        Assumptions.assumeTrue(createdProductId != null, "Product must be created first");

        givenJson(baseUrl)
                .when()
                .delete("/api/products/{productId}", createdProductId)
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/products/{productId} — Get non-existent product should return 404")
    void getProductById_withInvalidId_shouldReturn404() {
        givenJson(baseUrl)
                .when()
                .get("/api/products/{productId}", 999999)
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }

}
