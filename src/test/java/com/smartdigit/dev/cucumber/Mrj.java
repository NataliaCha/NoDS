package com.smartdigit.dev.cucumber;

import com.google.common.collect.Lists;
import com.smartdigit.dev.generator.Rest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import freemarker.template.TemplateException;
import io.restassured.response.Response;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.smartdigit.dev.generator.Common.log;
import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

public class Mrj {


    private final static Properties envProperties = readEnvProperties();

    @Then("^Delete msg with \"([^\"]*)\"")
    public void deleteTestDates (String name)
    {
//        LocalDateTime today = LocalDateTime.now();
//        log("CALENDARTIME:"+ today);
        Response response = given()
                .contentType("accept: */*")
                .contentType("application/json")
                .body("{}")
                .when()
                .delete(envProperties.getProperty("td.qa.backend.host") + "mrj/"+name+"?environment=int")
                .then()
                .extract()
                .response();

//        response.prettyPrint();

        int rsStatus = response.statusCode();
        assertEquals("Response is failed", 200, rsStatus);

        log("Messages are deleted.");

    }

    @Given("^Insert asset with \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"")
    public void insertTestDate (String name, String eType, String pCount)
                {

            Response response = given()
                    .contentType("accept: */*")
                    .contentType("application/json")
                    .body("{\n"
                            +"\t\"name\":\"" + name
                            +"\",\n"
                            +"\t\"etype\":\"" + eType
                            +"\",\n"
                            +"\t\"dset\":\"{\\\"body\\\":{}}\",\n"
                            +"\t\"pcount\":\"" + pCount
                            +"\"\n" +
                            "}")
                    .when()
                    .post(envProperties.getProperty("td.qa.backend.host") + "mrj?environment=int")
                    .then()
                    .extract()
                    .response();

//            response.prettyPrint();

            int rsStatus = response.statusCode();
            assertEquals("Response is failed", 200, rsStatus);

            log("Messages are inserted.");

    }

    @When("^Sorting by name \"([^\"]*)\" and etype \"([^\"]*)\" with param \"([^\"]*)\" and result is$")
    public void sorting(String name, String etype, String param, List<String> result) throws IOException, TemplateException {

        Response response = Rest.get("mrj.host", "unmapped-data-packs/?errorType="+etype+"&sort=" +name+":"+param, Hooks.getUaaTokenMrj());
        log("!!SORTING CONTENT:\n" + response.jsonPath().getList("content."+name).stream().map(o -> (String) o).collect(Collectors.toList()));
        List<String> sortedList = response.jsonPath().getList("content."+name).stream().map(o -> (String) o).collect(Collectors.toList());

        int rsStatus = response.statusCode();
        assertEquals("Response is failed", 200, rsStatus);

        boolean compare;
        compare = sortedList.equals(result);
        System.out.println("Exception result: " + compare);
        log("Sort with " +name+ " successful.");
        assertEquals("Sorting is failed",true, compare);
    }

    @When("^Sorting with name \"([^\"]*)\" and etype \"([^\"]*)\" by param \"([^\"]*)\" is result$")
    public void sorting_by_TimeId (String name, String etype, String param, List<String> result) throws IOException, TemplateException {

        Response response = Rest.get("mrj.host", "unmapped-data-packs/?errorType="+etype+"&sort=" +name+":"+param, Hooks.getUaaTokenMrj());
        log("SORTING CONTENT:\n" + response.jsonPath().getList("content.tag").stream().map(o -> (String) o).collect(Collectors.toList()));
        List<String> sortedList = response.jsonPath().getList("content.tag").stream().map(o -> (String) o).collect(Collectors.toList());

        int rsStatus = response.statusCode();
        assertEquals("Response is failed", 200, rsStatus);

        boolean compare;
        compare = sortedList.equals(result);
        System.out.println("Exception result: " + compare);
        log("Sort with " +name+ " successful.");
        assertEquals("Sorting is failed",true, compare);
    }




    @When("^Sorting tag \"([^\"]*)\" by etype with name \"([^\"]*)\" and param \"([^\"]*)\" is result$")
    public void sorting_by_etype (String tag, String name, String param, List<String> result) throws IOException, TemplateException {


        Response response = Rest.get("mrj.host", "unmapped-data-packs/?tag="+tag+"&sort=" +name+":"+param, Hooks.getUaaTokenMrj());
        log("SORTING CONTENT:\n" + response.jsonPath().getList("content."+name).stream().map(o -> (String) o).collect(Collectors.toList()));
        List<String> sortedList = response.jsonPath().getList("content."+name).stream().map(o -> (String) o).collect(Collectors.toList());

        int rsStatus = response.statusCode();
        assertEquals("Response is failed", 200, rsStatus);

        boolean compare;
        compare = sortedList.equals(result);
        System.out.println("Exception result: " + compare);
        log("Sort with " +name+ " successful.");
        assertEquals("Sorting is failed",true, compare);
    }


    @When("^Sorting with filtred pageNumber \"([^\"]*)\" pageSize \"([^\"]*)\" with name \"([^\"]*)\" etype \"([^\"]*)\" by param \"([^\"]*)\" is result$")
    public void sorting_by_tag_with_PageNumber (String pageNumber, String pageSize, String name, String etype, String param, List<String> result) throws IOException, TemplateException {


        Response response = Rest.get("mrj.host", "unmapped-data-packs/?errorType="+etype+"&pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort=" + name + ":" + param, Hooks.getUaaTokenMrj());
        log("SORTING CONTENT:\n" + response.jsonPath().getList("content.tag").stream().map(o -> (String) o).collect(Collectors.toList()));
        List<String> sortedList = response.jsonPath().getList("content.tag").stream().map(o -> (String) o).collect(Collectors.toList());

        int rsStatus = response.statusCode();
        assertEquals("Response is failed", 200, rsStatus);

        boolean compare;
        compare = sortedList.equals(result);
        System.out.println("Exception result: " + compare);
        log("Sort with " + name + " successful.");
        assertEquals("Sorting is failed", true, compare);
    }




}
