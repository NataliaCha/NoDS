package com.smartdigit.dev.generator;

import io.restassured.response.Response;

import java.util.Properties;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static io.restassured.RestAssured.given;

public class Rest {

    private final static Properties envProperties = readEnvProperties();


    public static Response post(String srcHost ,String script){

        Response response = given()
                .contentType("accept: */*")
                .contentType("application/json")
                .body(script)
                .when()
                .post(envProperties.getProperty(srcHost))
                .then()
                .extract()
                .response();

        return response;

    }

    public static Response post(String srcHost ,String script, String token){

        Response response = given()
                .contentType("accept: */*")
                .contentType("application/json")
                .auth().oauth2(token)
                .body(script)
                .when()
                .post(envProperties.getProperty(srcHost))
                .then()
                .extract()
                .response();

        return response;

    }

    public static Response get(String srcHost, String srcTail){
        Response response = given()
                .contentType("application/json")
                .when()
                .get(envProperties.getProperty(srcHost) + srcTail)
                .then()
                .extract()
                .response();

        return response;
    }

    public static Response get(String srcHost, String srcTail, String token){
        Response response = given()
                .contentType("application/json")
                .auth().oauth2(token)
                .when()
                .get(envProperties.getProperty(srcHost) + srcTail)
                .then()
                .extract()
                .response();

        return response;
    }

    public static void postToQa(String name, String env, String status, String comment){
        Response response = given()
                .contentType("accept: */*")
                .contentType("application/json")
                .body("{\"name\":\""+name+"\", \"env\": \""+env+"\", \"status\":\""+status+"\",\t\"comment\":\""+comment+"\"}")
                .when()
                .post(envProperties.getProperty("td.qa.backend.host") + "qa")
                .then()
                .extract()
                .response();
    }

}
