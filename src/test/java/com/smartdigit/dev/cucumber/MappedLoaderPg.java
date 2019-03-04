package com.smartdigit.dev.cucumber;

import com.smartdigit.dev.generator.Rest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static io.restassured.RestAssured.given;
import static com.smartdigit.dev.generator.Common.log;

public class MappedLoaderPg {

    private final static Properties envProperties = readEnvProperties();

    @When("^Create mapping og \"([^\"]*)\" and assets$")
    public void createMappingOgAndAssets(String ogName, List<String> assets) {

        String assetsLine = "";

        for (String asset:assets) {
            assetsLine = assetsLine + "{\"tag\":\"" + asset + "\"},";
        }

        assetsLine = "[" + assetsLine.substring(0, assetsLine.length()-1) + "]";

        Response response =
                given()
                        .contentType("application/json")
                        .auth().oauth2(Hooks.getUaaTokenMds())
                        .body(assetsLine)
                        .when()
                        .post(envProperties.getProperty("mlpg.host") + ogName)
                        .then()
                        .extract()
                        .response();

        assertEquals("Response is failed", 200, response.statusCode());
        log("Mapping created for go [" + ogName + "]");

    }

    @Then("^Asset \"([^\"]*)\" mapped with og \"([^\"]*)\"$")
    public void assetMappedWithOg(String asset, String ogName) {
        Response response = Rest.get("mlpg.host", ogName, Hooks.getUaaTokenMds());

        response.prettyPrint();

        assertEquals("Response is failed", 200, response.statusCode());

        List assetsList = response.path("tag");

        assertTrue("Asset ["+asset+"] is not mapped with og ["+ogName+"]",assetsList.contains(asset));

        log("Asset ["+asset+"] mapped with og ["+ogName+"]");
    }

    @Then("Tag \"([^\"]*)\" with quality \"([^\"]*)\" and value \"([^\"]*)\" is in DB")
    public void tagWithQualityAndValueIsInDB(String tagName, String quality, String value) {
        Response response = given()
                .contentType("application/json")
                .queryParam("db",envProperties.getProperty("db.name"))
                .queryParam("user",envProperties.getProperty("db.user"))
                .queryParam("password",envProperties.getProperty("db.password"))
                .when()
                .get(envProperties.getProperty("td.qa.backend.host") + "mapped-data/" + tagName)
                .then()
                .extract()
                .response();

        assertEquals("Response is failed", 200, response.statusCode());

        response.prettyPrint();

        assertEquals("The latest tag is not ["+tagName+"].", tagName, response.path("tag_name").toString());
        assertEquals("The latest quality is not ["+quality+"].", quality, response.path("qual").toString());
        assertEquals("The latest value is not ["+value+"].", value, response.path("val").toString());
        log("The latest tag is ["+tagName+"] with quality ["+quality+"] and value ["+value+"]");
    }


    @Given ("Delete all tags \"([^\"]*)\" and quality \"([^\"]*)\" from DB")
    public void deleteAllTagsFromDb(String tagName, String quality) {
        Response response = given()
                .contentType("application/json")
                .queryParam("db",envProperties.getProperty("db.name"))
                .queryParam("user",envProperties.getProperty("db.user"))
                .queryParam("password",envProperties.getProperty("db.password"))
                .when()
                .delete(envProperties.getProperty("td.qa.backend.host") + "mapped-data/" + tagName)
                .then()
                .extract()
                .response();

        assertEquals("Response is failed", 200, response.statusCode());

        log("Tags ["+tagName+"] are deleted from BD mapped-data");
    }
}
