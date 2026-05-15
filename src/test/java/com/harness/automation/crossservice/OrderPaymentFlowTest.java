package com.harness.automation.crossservice;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Cross-Service — Order → Payment Flow Tests")
class OrderPaymentFlowTest extends BaseApiTest {

    @Value("${services.order.base-url}")
    private String orderBaseUrl;

    @Value("${services.payment.base-url}")
    private String paymentBaseUrl;

    private static Integer orderId;
    private static Integer paymentId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Create an order")
    void step1_createOrder() {
        String body = """
                {
                    "orderDate": "2026-05-15 10:00:00",
                    "orderDesc": "Cross-service test order",
                    "orderFee": 250.00,
                    "cart": {
                        "cartId": 1
                    }
                }
                """;

        orderId = givenJson(orderBaseUrl)
                .body(body)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("orderDesc", equalTo("Cross-service test order"))
                .extract()
                .path("orderId");
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Create a payment linked to the order")
    void step2_createPaymentForOrder() {
        Assumptions.assumeTrue(orderId != null, "Order must be created in step 1");

        String body = String.format("""
                {
                    "isPayed": false,
                    "paymentStatus": "NOT_STARTED",
                    "order": {
                        "orderId": %d
                    }
                }
                """, orderId);

        paymentId = givenJson(paymentBaseUrl)
                .body(body)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(200)
                .body("isPayed", equalTo(false))
                .extract()
                .path("paymentId");
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify payment references the correct order")
    void step3_verifyPaymentLinkedToOrder() {
        Assumptions.assumeTrue(paymentId != null, "Payment must be created in step 2");

        givenJson(paymentBaseUrl)
                .when()
                .get("/api/payments/{paymentId}", paymentId)
                .then()
                .statusCode(200)
                .body("paymentId", equalTo(paymentId))
                .body("order.orderId", equalTo(orderId));
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Complete the payment")
    void step4_completePayment() {
        Assumptions.assumeTrue(paymentId != null, "Payment must be created in step 2");

        String body = String.format("""
                {
                    "paymentId": %d,
                    "isPayed": true,
                    "paymentStatus": "COMPLETED",
                    "order": {
                        "orderId": %d
                    }
                }
                """, paymentId, orderId);

        givenJson(paymentBaseUrl)
                .body(body)
                .when()
                .put("/api/payments")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Cleanup — delete payment and order")
    void step5_cleanup() {
        if (paymentId != null) {
            givenJson(paymentBaseUrl)
                    .when()
                    .delete("/api/payments/{paymentId}", paymentId)
                    .then()
                    .statusCode(200);
        }
        if (orderId != null) {
            givenJson(orderBaseUrl)
                    .when()
                    .delete("/api/orders/{orderId}", orderId)
                    .then()
                    .statusCode(200);
        }
    }

}
