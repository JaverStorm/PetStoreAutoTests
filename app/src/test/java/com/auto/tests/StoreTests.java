package com.auto.tests;

import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;
import static io.restassured.RestAssured.given;
import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;


@TestMethodOrder(OrderAnnotation.class)
public class StoreTests extends BaseClass {

    @Test
    @Order(1)
    @DisplayName("GET /store/inventory - получение текущего инвентаря")
    public void testInventory() {
        given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .body("available", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    @DisplayName("POST /store/order - создание нового заказа с валидными данными")
    public void testPlaceOrder() {
        String newOrder = "{ \"id\": 1, \"petId\": 123450, \"quantity\": 2, \"shipDate\": \"2025-11-08T16:07:21.524Z\"," +
                " \"status\": \"placed\", \"complete\": true }";
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .body("status", equalTo("placed"));
    }

    // Краш, принимается любой status (в том числе числовое значение), ожидание - код 400, фактически - код 200.
    @Test
    @Order(3)
    @DisplayName("POST /store/order - невалидный статус 'invalid' (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testPlaceOrderWithInvalidStatus() {
        String newOrder = "{ \"id\": 200, \"petId\": 123450, \"quantity\": 1, \"shipDate\": \"2025-11-08T16:07:21.524Z\"," +
                " \"status\": \"invalid\", \"complete\": true }";
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(400);
    }

    // Краш, сервер принимает petId = -1, ожидание - код 400, фактически - код 200.
    @Test
    @Order(4)
    @DisplayName("POST /store/order - заказ с petId = -1 (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testPlaceInvalidPetIdOrder() {
        String newOrder = "{ \"id\": 2, \"petId\": -1, \"quantity\": 2, \"shipDate\": \"2025-11-08T16:07:21.524Z\", " +
                "\"status\": \"placed\", \"complete\": true }";
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(5)
    @DisplayName("POST /store/order - некорректный JSON (Негативный тест)")
    public void testPlaceInvalidOrder() {
        String newOrder = "{ \"id\": 2, \"petId\": 123450, \"quantity\": 2, \"shipDate\": , \"status\": \"placed\", \"complete\": true }";
        given()
                .header("Content-Type", "application/json")
                .body(newOrder)
                .when()
                .post("/store/order")
                .then()
                .statusCode(400);
    }
    // Вынуждено добавлен повтор попытки и таймаут, в первую попытку сервер почти всегда возвращает 404, со второй - находит существующий заказ
    @Test
    @Order(6)
    @DisplayName("GET /store/order/{id} - получение существующего заказа по ID")
    public void testGetOrderById() {
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
        given()
                .when()
                .get("/store/order/{orderId}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
                });
    }

    @Test
    @Order(7)
    @DisplayName("GET /store/order/{id} - попытка получить несуществующий заказ (Негативный тест)")
    public void testGetNonExistingOrder() {
        given()
                .when()
                .get("/store/order/{orderId}", 9999)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(8)
    @DisplayName("GET /store/order/{id} - некорректный ID (Негативный тест, строка вместо числа)")
    public void testGetUnCorrectOrder() {
        given()
                .when()
                .get("/store/order/{orderId}", "MMM")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404)));
    }

    @Test
    @Order(9)
    @DisplayName("DELETE /store/order/{id} - удаление несуществующего заказа (Негативный тест)")
    public void testDeleteNotFoundOrder() {
        given()
                .when()
                .delete("/store/order/{orderId}", 99999999)
                .then()
                .statusCode(404);
    }

    // Несоответствие кода ответа, некорректный ID обрабатывается с ошибкой "не найденный ресурс".
    @Test
    @Order(10)
    @DisplayName("DELETE /store/order/{id} - удаление с отрицательным ID (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testDeleteInvalidOrder() {
        given()
                .when()
                .delete("/store/order/{orderId}", -1)
                .then()
                .statusCode(400);
    }

    @Test
    @Order(11)
    @DisplayName("DELETE /store/order/{id} - удаление существующего заказа")
    public void testDeleteOrder() {


        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
        given()
                .when()
                .delete("/store/order/{orderId}", 1)
                .then()
                .statusCode(200);
                });
    }

}