package com.harness.automation.product;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product Service — Category CRUD API Tests")
class CategoryCrudApiTest extends BaseApiTest {

    @Value("${services.product.base-url}")
    private String baseUrl;

    private static Integer createdCategoryId;

    @Test
    @Order(1)
    @DisplayName("POST /api/categories — Create category should return 200 with CategoryDto")
    void createCategory_shouldReturn200WithCategoryDto() {
        String body = """
                {
                    "categoryTitle": "Test Category",
                    "imageUrl": "https://example.com/cat.png"
                }
                """;

        createdCategoryId = givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/categories")
                .then()
                .statusCode(200)
                .body("categoryTitle", equalTo("Test Category"))
                .extract()
                .path("categoryId");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/categories — Get all categories should return category list")
    void getAllCategories_shouldReturnCategoryList() {
        givenJson(baseUrl)
                .when()
                .get("/api/categories")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/categories/{categoryId} — Get category by ID should return CategoryDto")
    void getCategoryById_shouldReturnCategoryDto() {
        Assumptions.assumeTrue(createdCategoryId != null, "Category must be created first");

        givenJson(baseUrl)
                .when()
                .get("/api/categories/{categoryId}", createdCategoryId)
                .then()
                .statusCode(200)
                .body("categoryId", equalTo(createdCategoryId));
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /api/categories/{categoryId} — Delete category should return 200")
    void deleteCategoryById_shouldReturn200() {
        Assumptions.assumeTrue(createdCategoryId != null, "Category must be created first");

        givenJson(baseUrl)
                .when()
                .delete("/api/categories/{categoryId}", createdCategoryId)
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

}
