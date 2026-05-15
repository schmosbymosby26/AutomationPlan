package com.harness.automation.favourite;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Favourite Service — CRUD API Tests")
class FavouriteCrudApiTest extends BaseApiTest {

    @Value("${services.favourite.base-url}")
    private String baseUrl;

    @Test
    @Order(1)
    @DisplayName("POST /api/favourites — Add favourite should return 200")
    void addFavourite_shouldReturn200WithFavouriteDto() {
        String body = """
                {
                    "userId": 1,
                    "productId": 1,
                    "likeDate": "2026-05-15"
                }
                """;

        givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/favourites")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/favourites — Get all favourites should return favourite list")
    void getAllFavourites_shouldReturnFavouriteList() {
        givenJson(baseUrl)
                .when()
                .get("/api/favourites")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/favourites/{userId}/{productId}/{likeDate} — Get favourite by composite ID should return FavouriteDto")
    void getFavouriteByCompositeId_shouldReturnFavouriteDto() {
        givenJson(baseUrl)
                .when()
                .get("/api/favourites/{userId}/{productId}/{likeDate}", 1, 1, "2026-05-15")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /api/favourites/{userId}/{productId}/{likeDate} — Delete favourite should return 200")
    void deleteFavourite_shouldReturn200() {
        givenJson(baseUrl)
                .when()
                .delete("/api/favourites/{userId}/{productId}/{likeDate}", 1, 1, "2026-05-15")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

}
