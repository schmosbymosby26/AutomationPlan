package com.harness.automation.crossservice;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Cross-Service — Order → User Resolution Flow Tests")
class OrderUserFlowTest extends BaseApiTest {

    @Value("${services.order.base-url}")
    private String orderBaseUrl;

    @Value("${services.user.base-url}")
    private String userBaseUrl;

    private static Integer userId;
    private static Integer cartId;
    private static Integer orderId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Create a user")
    void step1_createUser() {
        String body = """
                {
                    "firstName": "Flow",
                    "lastName": "TestUser",
                    "imageUrl": "https://example.com/flow.png",
                    "email": "flowtest@automation.com",
                    "phone": "5551234567",
                    "credential": {
                        "username": "flowtest_user",
                        "password": "securePass123",
                        "roleBasedAuthority": "ROLE_USER",
                        "isEnabled": true,
                        "isAccountNonExpired": true,
                        "isAccountNonLocked": true,
                        "isCredentialsNonExpired": true
                    }
                }
                """;

        userId = givenJson(userBaseUrl)
                .body(body)
                .when()
                .post("/api/users")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Flow"))
                .extract()
                .path("userId");
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Create a cart for the user")
    void step2_createCartForUser() {
        Assumptions.assumeTrue(userId != null, "User must be created in step 1");

        String body = String.format("""
                {
                    "userId": %d
                }
                """, userId);

        cartId = givenJson(orderBaseUrl)
                .body(body)
                .when()
                .post("/api/carts")
                .then()
                .statusCode(200)
                .extract()
                .path("cartId");
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Create an order with the cart")
    void step3_createOrderWithCart() {
        Assumptions.assumeTrue(cartId != null, "Cart must be created in step 2");

        String body = String.format("""
                {
                    "orderDate": "2026-05-15 14:00:00",
                    "orderDesc": "Order for flow test user",
                    "orderFee": 75.50,
                    "cart": {
                        "cartId": %d
                    }
                }
                """, cartId);

        orderId = givenJson(orderBaseUrl)
                .body(body)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("orderDesc", equalTo("Order for flow test user"))
                .extract()
                .path("orderId");
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify order exists and cart is linked")
    void step4_verifyOrderHasCart() {
        Assumptions.assumeTrue(orderId != null, "Order must be created in step 3");

        givenJson(orderBaseUrl)
                .when()
                .get("/api/orders/{orderId}", orderId)
                .then()
                .statusCode(200)
                .body("orderId", equalTo(orderId))
                .body("cart", is(notNullValue()));
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify user still exists after order creation")
    void step5_verifyUserExists() {
        Assumptions.assumeTrue(userId != null, "User must be created in step 1");

        givenJson(userBaseUrl)
                .when()
                .get("/api/users/{userId}", userId)
                .then()
                .statusCode(200)
                .body("userId", equalTo(userId))
                .body("firstName", equalTo("Flow"));
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Cleanup — delete order, cart, and user")
    void step6_cleanup() {
        if (orderId != null) {
            givenJson(orderBaseUrl).delete("/api/orders/{orderId}", orderId);
        }
        if (cartId != null) {
            givenJson(orderBaseUrl).delete("/api/carts/{cartId}", cartId);
        }
        if (userId != null) {
            givenJson(userBaseUrl).delete("/api/users/{userId}", userId);
        }
    }

}
