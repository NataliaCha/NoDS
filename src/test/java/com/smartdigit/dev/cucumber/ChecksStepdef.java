package com.smartdigit.dev.cucumber;

import com.smartdigit.dev.generator.ConfigUtils;
import com.smartdigit.dev.generator.Rest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import java.util.List;
import java.util.Properties;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ChecksStepdef {


  private final static Properties envProperties = readEnvProperties();


  @Then("^Message with such \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" exists in TS$")
  public void checkDataPointInTimeSeries(String tag, String qual, String value) {

    Response response =
            given()
                    .contentType("application/json")
                    .header("Predix-Zone-Id", envProperties.getProperty("ts.zoneId"))
                    .header("Authorization", Hooks.getUaaToken())
                    .body("{\"start\": \"1mi-ago\",\"tags\":[{\"name\": \"" + tag + "\",\"order\": \"desc\",\"limit\": 1}]}")
                    .when()
                    .post(envProperties.getProperty("ts.host") + "v1/datapoints")
                    .then()
                    .contentType(JSON)
                    .extract()
                    .response();

    response.prettyPrint();

    assertEquals("Response is failed", 200, response.statusCode());

    List valuesList = response.path("tags[0].results[0].values[0]");

    if (String.valueOf(valuesList).contains(qual) && String.valueOf(valuesList).contains(value)) {
      System.out.println("Values value [" + value + "] and qual [" + qual + "] are exist in the income message");
    } else {
      assertTrue("There is no Expected value [" + value + "] in Received datapoint.", false);
    }

  }

  @Then("^Message with such \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" exists in Postgres$")
  public void checkDataPointInPostgress(String tag, String unit, String val) {

    String messageId = ConfigUtils.messageId;

    Response response = given()
            .contentType("application/json")
            .queryParam("messageId", messageId)
            .queryParam("tag", tag)
            .queryParam("access_token",Hooks.getUaaTokenMrj())
//            .queryParam("sort", "id:desc")
            .when()
            .get(envProperties.getProperty("postgress.host") + "unmapped-data-packs/")
            .then()
            .extract()
            .response();

    response.prettyPrint();

    assertEquals("Response is failed", 200, response.statusCode());

    List<String> content = response.path("content");

    if (content.size() == 0)
      assertFalse("JSON Request - content object is empty.", true);

    String firstJsonOfContent = response.path("content[0].json");

    JsonPath jsonBody = new JsonPath(firstJsonOfContent).setRoot("body");
    List<String> jsonDataPointVal = jsonBody.get("dataPoints.val");
    List<String> jsonDataPointMessageId = jsonBody.get("dataPoints.messageId");

    if (jsonDataPointVal.contains(val) && jsonDataPointMessageId.contains(messageId))
      assertTrue("Actual and expected val are not equals ", true);
  }



}

