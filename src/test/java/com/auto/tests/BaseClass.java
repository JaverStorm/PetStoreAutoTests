package com.auto.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseClass {

    @BeforeAll
    public static void globalSetup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }
}
