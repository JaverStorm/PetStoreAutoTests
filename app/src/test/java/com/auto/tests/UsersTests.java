package com.auto.tests;

import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersTests extends BaseClass {

    @Test
    @Order(1)
    @DisplayName("POST /user — создание нового пользователя с валидными данными")
    public void testCreateUser() {
        String newUser = "{ \"id\": 1, \"username\": \"andrew01\", \"firstName\": \"Andrey\", " +
                "\"lastName\": \"Andreev\", \"email\": \"andrew@example.ru\", \"password\": \"123321\", " +
                "\"phone\": \"123-456-7890\", \"userStatus\": 1 }";
        given()
                .header("Content-Type", "application/json")
                .body(newUser)
                .when()
                .post("/user")
                .then()
                .statusCode(200);
    }
    // Вынуждено добавлен повтор попытки и таймаут, в первую попытку сервер почти всегда возвращает 404, со второй - находит существующего пользователя
    @Test
    @Order(2)
    @DisplayName("GET /user/{username} — получение существующего пользователя по имени")
    public void testGetUserByUsername() {
        await().atMost(6, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).untilAsserted(() -> {
            given()
                    .when()
                    .get("/user/{username}", "andrew01")
                    .then()
                    .statusCode(200)
                    .body("username", equalTo("andrew01"));
        });
    }

    //Ошибка, успешная обработка невалидного формата username
    @Test
    @Order(3)
    @DisplayName("POST /user — создание пользователя с невалидным username (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testCreateUserWithInvalidUsername() {
        String newUser = "{ \"id\": 342, \"username\": \"\", \"firstName\": \"Igor\", " +
                "\"lastName\": \"Igorev\", \"email\": \"igor@example.ru\", \"password\": \"123321\", " +
                "\"phone\": \"123-456-7890\", \"userStatus\": 1 }";

        given()
                .header("Content-Type", "application/json")
                .body(newUser)
                .when()
                .post("/user")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    @DisplayName("POST /user/createWithArray — создание нескольких пользователей через массив")
    public void testCreateUsersWithArray() {
        String usersArray = "["
                + "{ \"id\": 654, \"username\": \"first\", \"firstName\": \"Andrey\", \"lastName\": \"Andreev\", " +
                "\"email\": \"andrew@example.ru\", \"password\": \"123321\", \"phone\": \"123-456-7890\", " +
                "\"userStatus\": 1 },"
                + "{ \"id\": 872, \"username\": \"second\", \"firstName\": \"Petr\", \"lastName\": \"Petrov\", " +
                "\"email\": \"petrov@example.ru\", \"password\": \"123321\", \"phone\": \"777-544-1233\", " +
                "\"userStatus\": 2 }"
                + "]";

        given()
                .header("Content-Type", "application/json")
                .body(usersArray)
                .when()
                .post("/user/createWithArray")
                .then()
                .statusCode(200);
    }


    @Test
    @Order(5)
    @DisplayName("GET /user/{username} — попытка получить несуществующего пользователя (Негативный тест)")
    public void testGetNonExistingUser() {
        given()
                .when()
                .get("/user/{username}", "nonexistentuser")
                .then()
                .statusCode(404);
    }

    //Неправильная интерпретация ошибки, невалидные данные обработаны как несуществующие. (См. тест №3)
    @Test
    @Order(6)
    @DisplayName("GET /user/{username} — запрос с невалидным именем пользователя (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testGetInvalidUsername() {
        given()
                .when()
                .get("/user/{username}", " ")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    @DisplayName("PUT /user/{username} — обновление данных существующего пользователя")
    public void testUpdateUser() {
        String updatedUser = "{ \"id\": 1, \"username\": \"andrew01\", \"firstName\": \"Andreyy\", " +
                "\"lastName\": \"Andreev\", \"email\": \"andrew@example.ru\", \"password\": \"123abc\", " +
                "\"phone\": \"123-456-7890\", \"userStatus\": 1 }";
        given()
                .header("Content-Type", "application/json")
                .body(updatedUser)
                .when()
                .put("/user/{username}", "andrew01")
                .then()
                .statusCode(200);
    }

    //Вместо возврата ошибки 404 создается новый пользователь с указанными данными
    @Test
    @Order(8)
    @DisplayName("PUT /user/{username} — обновление несуществующего пользователя (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testUpdateNonExistingUser() {
        String updatedUser = "{ \"id\": 1, \"username\": \"nocreated\", \"firstName\": \"Max\", " +
                "\"lastName\": \"Ivanov\", \"email\": \"ivanov@example.ru\", \"password\": \"98765\"," +
                " \"phone\": \"123-456-7890\", \"userStatus\": 1 }";

        given()
                .header("Content-Type", "application/json")
                .body(updatedUser)
                .when()
                .put("/user/{username}", "user")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    @DisplayName("PUT /user/{username} — обновление с невалидными данными (Негативный тест)")
    public void testUpdateUserWithInvalidData() {
        String invalidUser = "{ \"id\": 1, \"username\": asd23, \"firstName\": \"April\", \"lastName\":  }";

        given()
                .header("Content-Type", "application/json")
                .body(invalidUser)
                .when()
                .put("/user/{username}", " ")
                .then()
                .statusCode(400);
    }


    @Test
    @Order(10)
    @DisplayName("DELETE /user/{username} — попытка удалить несуществующего пользователя (Негативный тест)")
    public void testDeleteNonExistingUser() {
        given()
                .when()
                .delete("/user/{username}", "fakeuser")
                .then()
                .statusCode(404);
    }
    //API игнорирует невалидность username и пытается найти его. Вместо ошибки 400 возвращена 404.
    @Test
    @Order(11)
    @DisplayName("DELETE /user/{username} — удаление с невалидным именем пользователя (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testDeleteUserWithInvalidUsername() {
        given()
                .when()
                .delete("/user/{username}", "^^%$@#$")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(12)
    @DisplayName("GET /user/login — успешная авторизация пользователя с валидными данными")
    public void testLoginUser() {
        given()
                .queryParam("username", "andrew02")
                .queryParam("password", "123abc")
                .when()
                .get("/user/login")
                .then()
                .statusCode(200)
                .header("X-Expires-After", notNullValue())
                .header("X-Rate-Limit", notNullValue());
    }

    //сервер не проверяет корректность введенных данных, ожидаем код 400, получен код 200.
    @Test
    @Order(13)
    @DisplayName("GET /user/login — авторизация с некорректными данными (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testLoginUserWithInvalidCredentials() {
        given()
                .queryParam("username", "andrew02")
                .queryParam("password", "   ")
                .when()
                .get("/user/login")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(14)
    @DisplayName("GET /user/logout — успешный выход пользователя из системы")
    public void testLogoutUser() {
        given()
                .when()
                .get("/user/logout")
                .then()
                .statusCode(200);
    }
    // пустой массив обрабатывается с кодом 200, вместо 400
    @Order(15)
    @Test
    @DisplayName("POST /user/createWithArray — пустой массив (Негативный тест, фактический результат не соответствует ожидаемому)")
    public void testCreateUsersWithEmptyArray() {
        given()
                .header("Content-Type", "application/json")
                .body("[]")
                .when() // пустой
                .post("/user/createWithArray")
                .then()
                .statusCode(400);
    }


    @Order(16)
    @Test
    @DisplayName("POST /user/createWithList — создание пользователей списком")
    public void testCreateUsersList() {
        String usersList = "[" +
                "{ \"id\": 10, \"username\": \"list1\", \"firstName\": \"Pavel\", \"lastName\": \"Pavlov\", " +
                "\"email\": \"pavlov@example.ru\", \"password\": \"pass1\", \"phone\": \"111-111-1111\", \"userStatus\": 1 }," +
                "{ \"id\": 11, \"username\": \"list2\", \"firstName\": \"Semen\", \"lastName\": \"Semenov\", " +
                "\"email\": \"semenov@example.ru\", \"password\": \"pass2\", \"phone\": \"222-222-2222\", \"userStatus\": 2 }" +
                "]";
        given()
                .header("Content-Type", "application/json")
                .body(usersList)
                .when()
                .post("/user/createWithList")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(17)
    @DisplayName("DELETE /user/{username} — удаление существующего пользователя")
    public void testDeleteUser() {

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
        given()
                .when()
                .delete("/user/{username}", "andrew01")
                .then()
                .statusCode(200);
                });
    }

}
