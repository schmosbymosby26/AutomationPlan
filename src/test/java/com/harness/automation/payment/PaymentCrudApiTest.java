package com.harness.automation.payment;

import com.harness.automation.BaseApiTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Payment Service — CRUD API Tests")
class PaymentCrudApiTest extends BaseApiTest {

    @Value("${services.payment.base-url}")
    private String baseUrl;

    private static Integer createdPaymentId;

    @Test
    @Order(1)
    @DisplayName("POST /api/payments — Create payment should return 200 with PaymentDto")
    void createPayment_shouldReturn200WithPaymentDto() {
        String body = """
                {
                    "isPayed": false,
                    "paymentStatus": "NOT_STARTED",
                    "order": {
                        "orderId": 1
                    }
                }
                """;

        createdPaymentId = givenJson(baseUrl)
                .body(body)
                .when()
                .post("/api/payments")
                .then()
                .statusCode(200)
                .body("isPayed", equalTo(false))
                .body("paymentStatus", equalTo("NOT_STARTED"))
                .extract()
                .path("paymentId");
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/payments — Get all payments should return payment list")
    void getAllPayments_shouldReturnPaymentList() {
        givenJson(baseUrl)
                .when()
                .get("/api/payments")
                .then()
                .statusCode(200)
                .body("collection", is(notNullValue()))
                .body("collection.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/payments/{paymentId} — Get payment by ID should return PaymentDto")
    void getPaymentById_shouldReturnPaymentDto() {
        Assumptions.assumeTrue(createdPaymentId != null, "Payment must be created first");

        givenJson(baseUrl)
                .when()
                .get("/api/payments/{paymentId}", createdPaymentId)
                .then()
                .statusCode(200)
                .body("paymentId", equalTo(createdPaymentId));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/payments — Update payment should return updated PaymentDto")
    void updatePayment_shouldReturnUpdatedPaymentDto() {
        Assumptions.assumeTrue(createdPaymentId != null, "Payment must be created first");

        String body = String.format("""
                {
                    "paymentId": %d,
                    "isPayed": true,
                    "paymentStatus": "COMPLETED",
                    "order": {
                        "orderId": 1
                    }
                }
                """, createdPaymentId);

        givenJson(baseUrl)
                .body(body)
                .when()
                .put("/api/payments")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/payments/{paymentId} — Delete payment should return 200")
    void deletePaymentById_shouldReturn200() {
        Assumptions.assumeTrue(createdPaymentId != null, "Payment must be created first");

        givenJson(baseUrl)
                .when()
                .delete("/api/payments/{paymentId}", createdPaymentId)
                .then()
                .statusCode(200)
                .body(equalTo("true"));
    }

}
