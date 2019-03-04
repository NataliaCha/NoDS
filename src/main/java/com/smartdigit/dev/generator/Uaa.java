package com.smartdigit.dev.generator;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;


public class Uaa {

    public static String generateUaaToken(String url, String clientId, String clientSecret){
        Response response = given()
                .header("Pragma", "no-cache")
                .header("content-type", "application/x-www-form-urlencoded")
                .header("Cache-Control", "no-cache")
                .formParam("client_id",clientId)
                .formParam("grant_type","client_credentials")
                .formParam("client_secret",clientSecret)
                .when()
                .post(url)
                .then()
                .extract()
                .response();

        assertEquals("Response is failed", 200, response.statusCode());

        return response.path("access_token");
    }

}
