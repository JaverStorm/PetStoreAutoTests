package com.auto.tests;

import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static io.restassured.RestAssured.given;

@TestMethodOrder(OrderAnnotation.class)
public class PetTests extends BaseClass {

    @Test
    @Order(1)
    @DisplayName("POST /pet — добавление нового питомца")
    public void testAddNewPet() {
        String Pet = "{ \"id\": 123450, \"name\": \"Igor\", \"status\": \"available\" }";
        given()
                .header("Content-Type", "application/json")
                .body(Pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("Igor"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /pet/{id} — получение существующего питомца по ID")
    public void testGetExistingPet() {
        int petId = 123321000;
        String Pet = "{ \"id\": " + petId + ", \"name\": \"Barbos\", \"status\": \"available\" }";

        given()
                .header("Content-Type", "application/json")
                .body(Pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/pet/{petId}", petId)
                .then()
                .statusCode(200)
                .body("id", equalTo(petId));
    }

    @Test
    @Order(3)
    @DisplayName("GET /pet/{id} — попытка получить несуществующего питомца (Негативный тест)")
    public void testGetNotExistingPet() {
        given()
                .when()
                .get("/pet/{petId}", 999990999)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    @DisplayName("PUT /pet — обновление статуса существующего питомца")
    public void testUpdatePetStatus() {
        String updatedPet = "{ \"id\": 123450, \"name\": \"Igor\", \"status\": \"sold\" }";
        given()
                .header("Content-Type", "application/json")
                .body(updatedPet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("status", equalTo("sold"));
    }

    @Test
    @Order(5)
    @DisplayName("POST /pet/{id}/uploadImage — загрузка изображения питомца")
    public void testUploadPetImage() {
        given()
                .multiPart("file", "test.png")
                .when()
                .post("/pet/{petId}/uploadImage", 123450)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(6)
    @DisplayName("GET /pet/findByStatus?status=available — поиск питомцев статусу")
    public void testFindPetsByStatus() {
        given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("available")));
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /pet/{id} — удаление существующего питомца")
    public void testDeletePet() {
        given()
                .when()
                .delete("/pet/{petId}", 123450)
                .then()
                .statusCode(200);
    }

    @Test
    @Order(8)
    @DisplayName("POST /pet/{id} (form-data) — обновление несуществующего питомца (Негативный тест)")
    public void testUpdateNotExistingPetFormData() {
        int petId = 678678210;

        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", "Murzik")
                .formParam("status", "available")
                .when()
                .post("/pet/{petId}", petId)
                .then()
                .statusCode(404)
                .body("type", equalTo("unknown"))
                .body("message", equalTo("not found"));
    }

    @Test
    @Order(9)
    @DisplayName("GET /pet/findByStatus?status=available,sold — поиск питомцев по нескольким статусам")
    public void testFindPetsByTwinStatuses() {
        given()
                .queryParam("status", "available,sold")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(anyOf(equalTo("available"), equalTo("sold"))));
    }

    @Test
    @Order(10)
    @DisplayName("DELETE /pet/{id} — попытка удалить несуществующего питомца (Негативный тест)")
    public void testDeleteNotExistingPet() {
        given()
                .when()
                .delete("/pet/{petId}", 678678210)
                .then()
                .statusCode(404);
    }
}