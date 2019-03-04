package com.smartdigit.dev.cucumber;

import com.smartdigit.dev.generator.Count;
import com.smartdigit.dev.generator.Rest;
import com.smartdigit.dev.generator.template.FreemarkerEngine;
import com.smartdigit.dev.utils.CollectionUtils;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import freemarker.template.TemplateException;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.smartdigit.dev.generator.Common.log;
import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Asset {

  private final static Properties envProperties = readEnvProperties();


  @When("^Create asset with id \"([^\"]*)\"$")
  public void createAssetWithId(String assetName) throws IOException, TemplateException {
    FreemarkerEngine fe = new FreemarkerEngine();
    String script = fe.createAsset(assetName);

    Response response = Rest.post("asset.host",script);

    assertEquals("Response is failed",200, response.statusCode());
  }

  @When("^(?:Create|Edit) asset with")
  public void updateAssetWithUri(Map<String, String> argAsset) throws IOException, TemplateException {
    FreemarkerEngine fe = new FreemarkerEngine();
    String script = fe.updateAsset(argAsset);

    Response response = Rest.post("asset.host",script, Hooks.getUaaTokenAsset());

    assertEquals("Response is failed",200, response.statusCode());

    log("Operation with asset");
  }


  @When("^Delete asset with id \"([^\"]*)\"$")
  public void deleteAssetWithId(String assetId) {

    Response response = given()
            .contentType("application/json")
            .auth().oauth2(Hooks.getUaaTokenAsset())
            .when()
            .delete(envProperties.getProperty("asset.host") + assetId)
            .then()
            .extract()
            .response();

    assertEquals("Response is failed",200, response.statusCode());
    log("Delete asset "+assetId);
  }

  @When("Start assets loading from POA")
  public void startAssetsLoadingFromPOA() {
      given()
           .contentType("application/json")
           .auth().oauth2(Hooks.getUaaTokenAsset())
           .when()
           .post(envProperties.getProperty("asset.host") + "reload");
      log("Start loading from POA");
  }

  @Given("^Remember number of assets$")
  public void setAssetsCount() {
    Response response = Rest.get("td.qa.backend.host", "asset/count?environment=" + System.getProperty("env"));

    assertEquals("Response failed", 200, response.statusCode());

    int rsCount = response.jsonPath().get("count");

    Count.setCount(rsCount);
    log("Remember ["+rsCount+"] assets");
  }

  @Given("^Delete all assets$")
  public void deleteAllAssets() {
    Response response = Rest.get("td.qa.backend.host", "deleteall");

    assertEquals("Response failed", 200, response.statusCode());

    log("Delete all assets");

  }

  @And("^Wait assets are loaded$")
  public void waitAssetsLoaded() throws InterruptedException {
    int count = 0;
    int assetsCount = 0;

    while (count < 500) {
      Response response = Rest.get("asset.host", "synchronization-status", Hooks.getUaaTokenAsset());
      assertEquals("Response failed", 200, response.statusCode());

      String currStatus = response.jsonPath().get("status");
      log("Loading status:"+currStatus);
      assetsCount = response.jsonPath().get("lastSavedCounters.assetsCount");
      log("Current assets count:"+assetsCount);

      if (currStatus.equals("IDLE")) break;
      count++;

      Thread.sleep(5000);
    }

  }

//========================
//Checks =================
//========================

  @Given("Compare files")
  public void assetCompareFile() throws IOException {

//    try (Stream<String> stream = Files.lines(Paths.get("AssetsFromPOA_.txt"))) {
//      stream.forEach(System.out::println);
//    }

    List poaList = new ArrayList();
    List jvList = new ArrayList();
    List result = new ArrayList();

    try (BufferedReader br = new BufferedReader(new FileReader("AssetsFromPOA.txt"))) {
      String assetId;
      while ((assetId = br.readLine()) != null) {
        poaList.add(assetId);
      }
    }

    try (BufferedReader jvbr = new BufferedReader(new FileReader("jvAssetsFromPOA.txt"))) {
      String line;
      while ((line = jvbr.readLine()) != null) {
        jvList.add(line);
      }
    }

    System.out.println("poaList:" + poaList.size());
    System.out.println("jvList:" + jvList.size());

    int count = 0;
    boolean is;

    for (int i = 0; i < poaList.size(); i++) {
      is = false;
//      System.out.println(count + ":" + poaList.get(i));
      for (int j = 0; j < jvList.size(); j++) {
        if (poaList.get(i).equals(jvList.get(j))) {
          is = true;
          break;
        }
      }
      if(!is) System.out.println(poaList.get(i));
      count++;

    }

  }

  @Given("Asset exists in file")
  public void assetExistsInFile() throws IOException {

//    try (Stream<String> stream = Files.lines(Paths.get("AssetsFromPOA_.txt"))) {
//      stream.forEach(System.out::println);
//    }

    try (BufferedReader br = new BufferedReader(new FileReader("AssetsFromPOA.txt"))) {
      String assetId;
      while ((assetId = br.readLine()) != null) {

        Response response = Rest.get("asset.host", assetId);

        assertEquals("Response failed", 200, response.statusCode());

        String responseId = response.jsonPath().get("id");

        if(assetId.contentEquals(responseId)){
          System.out.println("|" + assetId+":"+responseId+"|equals|");
        }else {
          System.out.println("|" + assetId+":"+responseId+"|not-equals|");
        }

      }
    }

  }


  @Given("Asset with id \"([^\"]*)\" exists is (true|false)")
  public void assetWithIdExists(String assetId, String isExist) {
    Response response = Rest.get("asset.host", assetId, Hooks.getUaaTokenAsset());

    response.prettyPrint();

    assertEquals("Response failed", 200, response.statusCode());

    if (Boolean.valueOf(isExist)) {
      String responseId = response.jsonPath().get("id");
      assertTrue("Asset " + assetId + " doesn't exist", responseId.contains(assetId));
      log("Asset " + assetId + " exist");
    } else {
      String responseBody = response.getBody().asString();
      assertFalse("Asset " + assetId + " exists", responseBody.contains(assetId));
      log("Asset " + assetId + " doesn't exist");
    }

  }

  @Then("^Asset with id \"([^\"]*)\" has \"([^\"]*)\" : \"([^\"]*)\"$")
  public void assetWithIdHas(String assetId, String property, String propertyValue) {
    Response response = Rest.get("asset.host", assetId, Hooks.getUaaTokenAsset());

    assertEquals("Response failed", 200, response.statusCode());

    String rsBody = response.getBody().asString();
    assertTrue("There is no property " + property + " in the asset", rsBody.contains(property));

    String rsPropertyValue = response.jsonPath().get(property).toString();
    assertEquals("Properties of asset " + assetId
            + " aren't equals. Exp:"+propertyValue+ ", Actual:"+rsPropertyValue, propertyValue, rsPropertyValue);
    log("Properties of asset " + assetId + " are equals. Exp:"+propertyValue+ ", Actual:"+rsPropertyValue);

  }

  @Given("^Number of assets should be (\\d+)$")
  public void сountShouldBe(int value) {
    Response response = Rest.get("td.qa.backend.host", "count");

    assertEquals("Response failed", 200, response.statusCode());

    int rsCount = response.jsonPath().get("count");

    assertEquals("Number of assets is ["+rsCount+"] and input ["+value+"] not equals.", rsCount, value);
    log("Number of assets in memory ["+value+"] and in DB ["+rsCount+"] are equals.");
  }

  @Given("^Compare number of assets with memory$")
  public void compareAssetsNumWithMemory() {
    int memoryCount = Count.getCount();

    Response response = Rest.get("td.qa.backend.host", "count");

    assertEquals("Response failed", 200, response.statusCode());

    int rsCount = response.jsonPath().get("count");

    assertEquals("Number of assets in memory ["+memoryCount+"] and in DB ["+rsCount+"] not equals.", memoryCount, rsCount);
    log("Number of assets in memory ["+memoryCount+"] and in DB ["+rsCount+"] are equals.");
  }


  @Then("^Asset with id \"([^\"]*)\" has children \"([^\"]*)\"$")
  public void assetWithIdHasChildren(String assetId, String childId) {
    Response response = Rest.get("asset.host", assetId + "/children", Hooks.getUaaTokenAsset());

    assertEquals("Response failed", 200, response.statusCode());

    JsonPath tempJson = response.jsonPath();
    List<String> ids = tempJson.get("id");

    boolean isChild = false;

    for(String id: ids){
      if(id.contains(childId)) {
        isChild = true;
        break;
      }
    }

    assertTrue(childId + " is not child of " + assetId, isChild);

  }

  @Given ("^Count assets in POA$")
  public void countAssetsInPoa() throws IOException {
    List<String> urisFinal = new ArrayList<String>();

    String linkForRemove = "https://asset-query-rosneft-scpdr-core-integration.run.aws-usw02-pr.ice.predix.io/asset/?pageSize=1000&fields=uri&cursorState=";
    String headerLink = "";
    int count = 0;

    Writer output;
    output = new BufferedWriter(new FileWriter("AssetsFromPOA.txt",true));  //clears file every time

    while(count <200000) {
      Response response = given()
              .contentType("application/json")
              .header("Authorization", envProperties.getProperty("asset.poa.token"))
              .queryParam("pageSize", "1000")
              .queryParam("cursorState", headerLink)
              .queryParam("fields", "uri")
              .when()
              .get(envProperties.getProperty("asset.poa.host") + "asset/")
              .then()
              .extract()
              .response();

//      response.prettyPrint();

      JsonPath tempJson = response.jsonPath();
      List<String> uris = tempJson.get("uri");

      for (String line:uris) {
        urisFinal.add(line);
        output.append(line+";");
      }

      System.out.println(count);

      String rsLinkHeader = response.getHeader("Link");

      if(rsLinkHeader == null) break;
      headerLink = rsLinkHeader.replace(linkForRemove,"");
      headerLink = headerLink.replace(">;rel=next","");

      count++;
    }

    System.out.println("Count:"+count);
    output.close();

  }


  @Then("^Asset with id \"([^\"]*)\" is root asset$")
  public void assetWithIdIsRoot(String assetId) {
    Response response = Rest.get("asset.host", "roots/", Hooks.getUaaTokenAsset());

    assertEquals("Response failed", 200, response.statusCode());

    JsonPath tempJson = response.jsonPath();
    List<String> ids = tempJson.get("id");

    boolean isExistsService = false;

    for(String id: ids){
      if(id.equals(assetId)) {
        isExistsService = true;
        break;
      }
    }

    response = Rest.get("td.qa.backend.host", "asset/children/root");

    assertEquals("Response failed", 200, response.statusCode());

    tempJson = response.jsonPath();
    ids = tempJson.get("id");

    boolean isExistsPostgres = false;

    for(String id: ids){
      if(id.equals(assetId)) {
        isExistsPostgres = true;
        break;
      }
    }

    assertTrue("Asset with id ["+assetId+"] is not root asset.", isExistsService && isExistsPostgres);
    log("Asset with id ["+assetId+"] is root asset.");
  }

  @Then("^Asset \"([^\"]*)\" on page \"([^\"]*)\" with (\\d+) page size$")
  public void assetExistOnSpecifiedPage(String assetId, String pageNumber, int pageSize) {

    if(pageNumber.equals("count")) pageNumber = String.valueOf(Count.getCount());

    Response response = Rest.get("asset.host", "flat/?pageNumber="+pageNumber+"&pageSize="+pageSize, Hooks.getUaaTokenAsset());

    assertEquals("Response failed", 200, response.statusCode());

    JsonPath tempJson = response.jsonPath();
    List<String> ids = tempJson.get("id");

    boolean isExistsService = false;

    for(String id: ids){
      if(id.equals(assetId)) {
        isExistsService = true;
        break;
      }
    }

    assertTrue("Asset with id ["+assetId+"] is not root asset.", isExistsService);
    log("Asset with id ["+assetId+"] is on the page");
  }

  @When("^Get all assets as flat with pageNumber \"([^\"]*)\" and pageSize \"([^\"]*)\" result is by \"([^\"]*)\" count \"([^\"]*)\"")
  public void getAllAsetsAsFlat (String pageNumber, int pageSize, String by,int count) {


    Response response = Rest.get("asset.host", "flat?pageNumber="+pageNumber+"&pageSize="+pageSize, Hooks.getUaaTokenAsset());

    assertEquals("Response failed", 200, response.statusCode());

    List responseList = response.path("data."+by);


    boolean compare;
    int result_count = responseList.size();
    compare = (result_count==count);
    assertEquals("Pagination when get assets flat failed", true, compare);

  }

  @Then("^Search flat with assetId \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlatOnlyId (String assetId, String searchId, int totalElements) {

    Response response = given()
            .contentType("application/json")
            .body("{  \n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\"\n" +
                    "  ],\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();


    assertEquals("Response failed", 200, response.statusCode());
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("searchId: " + resultIds);
    Integer countElements = response.jsonPath().get("totalElements");
    log("totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with assetId \"([^\"]*)\" and assetId2 \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlatTwoId (String assetId, String assetId2,String searchId, int totalElements) {

    Response response = given()
            .contentType("application/json")
            .body("{ \n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\",\n" +
                    "    \"" +
                    assetId2 +
                    "\"\n" +
                    "  ],\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();


    assertEquals("Response failed", 200, response.statusCode());
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("searchId: " + resultIds);
    Integer countElements = response.jsonPath().get("totalElements");
    log("totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlatNameAndId (String namePattern, String assetId, String searchId, int totalElements) {

    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\", \n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\"   \n" +
                    "  ],\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();


    assertEquals("Response failed", 200, response.statusCode());
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("searchId: " + resultIds);
    Integer countElements = response.jsonPath().get("totalElements");
    log("totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" and assetId2 \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlatNameAndTwoId (String namePattern, String assetId, String assetId2,String searchId, int totalElements) {

    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\", \n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\",\n" +
                    "    \"" +
                    assetId2 +
                    "\"\n" +
                    "  ],\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();


    assertEquals("Response failed", 200, response.statusCode());
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("searchId: " + resultIds);
    Integer countElements = response.jsonPath().get("totalElements");
    log("totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with assetId \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlat (String assetId, String children, String searchId, int totalElements) {



//    Response response = Rest.post("asset.host", "search/flat", Hooks.getUaaTokenAsset());

    Response response = given()
            .contentType("application/json")
//            .header("Authorization", envProperties.getProperty("asset.poa.token"))
//            .queryParam("pageSize", "1000")
//            .queryParam("cursorState", headerLink)
//            .queryParam("fields", "uri")
            .body("{  \n" +
                    "  \"ids\": [\n" +
                    "    \"" +assetId+
                    "\"\n" +
                    "  ],\n" +
                    "  \n" +
                    "  \"includeChildren\": "+children+ ",\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();

//    response.prettyPrint();

    assertEquals("Response failed", 200, response.statusCode());
//    String messageBody = ehmessage.getBody().toStringUtf8();
//    JSONObject json = new JSONObject(messageBody);
//    JSONArray bodyArray = json.getJSONArray("body");

//    List results = response.path("data.id");
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("searchId: " + resultIds);


//    String results = response.jsonPath().get("data.id[0]");
    Integer countElements = response.jsonPath().get("totalElements");
//    log("searchId: " + results);
    log("totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with namePattern \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlatNamepathAndChildren (String namePattern, String children, String searchId, int totalElements) {

    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\", \n" +
                    "  \"includeChildren\": " +
                    children +
                    ",\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();

    assertEquals("Response failed", 200, response.statusCode());
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!searchId: " + resultIds);


    Integer countElements = response.jsonPath().get("totalElements");
    log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" totalElements \"([^\"]*)\"")
  public void searchFlatNameIdAndChildren (String namePattern, String assetId,String children, String searchId, int totalElements) {

    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\", \n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\"      \n" +
                    "  ],\n" +
                    "  \"includeChildren\": " +
                    children +
                    ",\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();

    assertEquals("Response failed", 200, response.statusCode());
    List resultIdCollections = response.path("data.id");
    String resultIds = CollectionUtils.flattenToSet(resultIdCollections).toString();
    log("searchId: " + resultIds);


    Integer countElements = response.jsonPath().get("totalElements");
    log("totalElements: " + countElements);

    boolean compare;
    compare = resultIds.contains(searchId)&&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }

  @Then("^Search flat with namePattern \"([^\"]*)\" is result totalElements \"([^\"]*)\" and listId$")
  public void searchFlatPar (String namePattern, int totalElements, List<String> listId) {

    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\",\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/flat")
            .then()
            .extract()
            .response();

//    response.prettyPrint();

    assertEquals("Response failed", 200, response.statusCode());
//    log("SORTING CONTENT:\n" + response.jsonPath().getList("data.id").stream().map(o -> (String) o).collect(Collectors.toList()));
//    List<String> results = response.jsonPath().getList("data.id").stream().map(o -> (String) o).collect(Collectors.toList());

    List results = response.path("data.id");
//    String results = response.jsonPath().get("data.id[0]");
    Integer countElements = response.jsonPath().get("totalElements");
    log("totalElements: " + countElements);
    log("RESULT_LIST: " + results);

    boolean compare;
    compare = results.containsAll(listId)&countElements.equals(totalElements);
//    compare = results.equals(searchId)&countElements.equals(totalElements);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);

  }


    @Then("^Search tree with assetId \"([^\"]*)\" and assetId2 \"([^\"]*)\" is result searchId \"([^\"]*)\" childrenId \"([^\"]*)\"")
    public void assetTwoId(String assetId, String assetId2, String searchId, String childrenId) {
        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \n" +
                        "  \"ids\": [\n" +
                        "    \"" +
                        assetId +
                        "\",\n" +
                        "\"" +
                        assetId2 +
                        "\"\n" +
                        "  ]\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();
//        response.prettyPrint();
        assertEquals("Response failed", 200, response.statusCode());

        String resultsId = response.jsonPath().get("id[0]");
        List resultChildrenIdsCollections = response.path("children.id");
        String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
        log("resultId: " + resultsId);
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultsId.equals(searchId)&&resultChildrenIds.contains(childrenId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

    @Then("^Search tree with assetId \"([^\"]*)\" is result searchId \"([^\"]*)\" and has't field \"([^\"]*)\"")
    public void assetOnlyId(String assetId, String searchId, String field) {
        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"ids\": [\n" +
                        "    \"" +
                        assetId +
                        "\"\n" +
                        "  ]  \n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();

        assertEquals("Response failed", 200, response.statusCode());

        String rsBody = response.getBody().asString();
        assertFalse("Asset have field" + field , rsBody.contains(field));

//        String resultsId = response.jsonPath().get("id");
        List resultChildrenIdsCollections = response.path("id");
        String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
//        log("resultId: " + resultsId);
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultChildrenIds.contains(searchId);
//        compare = resultsId.equals(searchId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

    @Then("^Search tree with namePattern \"([^\"]*)\" is result searchId \"([^\"]*)\" childrenId \"([^\"]*)\"")
    public void searchTreePattern (String namePattern, String searchId, String childrenId) {

        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"namePattern\": \"" +
                        namePattern +
                        "\"\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();

//        response.prettyPrint();
        assertEquals("Response failed", 200, response.statusCode());

        String resultsId = response.jsonPath().get("id[0]");
        List resultChildrenIdsCollections = response.path("children.id");
        String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
        log("resultId: " + resultsId);
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultsId.equals(searchId)&&resultChildrenIds.contains(childrenId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

    @Then("^Search tree with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" is result searchId \"([^\"]*)\" and has't field \"([^\"]*)\"")
    public void assetNameAndId(String namePattern, String assetId, String searchId, String field) {
        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"namePattern\": \"" +
                        namePattern +
                        "\",\n" +
                        "  \"ids\": [\n" +
                        "    \"" +
                        assetId +
                        "\"\n" +
                        "  ]\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();

        assertEquals("Response failed", 200, response.statusCode());

        String rsBody = response.getBody().asString();
        assertFalse("Asset have field" + field , rsBody.contains(field));

//        String resultsId = response.jsonPath().get("id");
        List resultChildrenIdsCollections = response.path("id");
        String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
//        log("resultId: " + resultsId);
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultChildrenIds.contains(searchId);
//        compare = resultsId.equals(searchId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

    @Then("^Search tree with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" and assetId2 \"([^\"]*)\" is result searchId \"([^\"]*)\" and has't field \"([^\"]*)\"")
    public void assetNameAndIdandOtherId(String namePattern, String assetId, String assetId2, String searchId, String field) {
        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"namePattern\": \"" +
                        namePattern +
                        "\",\n" +
                        "  \"ids\": [\n" +
                        "    \"" +
                        assetId +
                        "\", \"" +
                        assetId2 +
                        "\"\n" +
                        "  ]\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();

        assertEquals("Response failed", 200, response.statusCode());

        String rsBody = response.getBody().asString();
        assertFalse("Asset have field" + field , rsBody.contains(field));

        List resultChildrenIdsCollections = response.path("id");
        String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultChildrenIds.contains(searchId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

    @Then("^Search tree with assetId \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" childrenId \"([^\"]*)\"")
    public void searchTreeIdAndChildren (String assetId, String children, String searchId, String childrenId) {


        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"ids\": [\n" +
                        "    \"" +
                        assetId +
                        "\"\n" +
                        "  ],\n" +
                        "  \"includeChildren\": " +
                        children +
                        "\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();

//    response.prettyPrint();
        assertEquals("Response failed", 200, response.statusCode());

        String resultsId = response.jsonPath().get("id[0]");
        List resultChildrenIdsCollections = response.path("children.id");
        Set resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections);
        log("resultId: " + resultsId);
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultsId.equals(searchId)&&resultChildrenIds.contains(childrenId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

  @Then("^Search tree with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" childrenId \"([^\"]*)\"")
  public void searchTree (String namePattern, String assetId, String children, String searchId, String childrenId) {

//    Response response = Rest.post("asset.host", "search/flat", Hooks.getUaaTokenAsset());

    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\",\n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\"\n" +
                    "  ],\n" +
                    "  \n" +
                    "  \"includeChildren\": " +
                    children +
                    "\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/tree")
            .then()
            .extract()
            .response();

//    response.prettyPrint();
    assertEquals("Response failed", 200, response.statusCode());

    String resultsId = response.jsonPath().get("id[0]");
    List resultChildrenIdsCollections = response.path("children.id");
    Set resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections);
    log("resultId: " + resultsId);
    log("resultChildrenId: " + resultChildrenIds);

    boolean compare;
    compare = resultsId.equals(searchId)&&resultChildrenIds.contains(childrenId);
    System.out.println("Exception result: " + compare);
    assertEquals("Search is failed", true, compare);
  }

    @Then("^Search tree with namePattern \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" and has't field \"([^\"]*)\"")
    public void assetWithIdHasNot(String namePattern, String children, String searchId, String field) {
        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"namePattern\": \"" +
                        namePattern +
                        "\",\n" +
                        "  \"includeChildren\": " +
                        children +
                        "\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();
//        response.prettyPrint();
        assertEquals("Response failed", 200, response.statusCode());

        String rsBody = response.getBody().asString();
        assertFalse("Asset have field" + field , rsBody.contains(field));

    }

  @Then("^Search tree with assetId \"([^\"]*)\" and children \"([^\"]*)\" is result has't field \"([^\"]*)\"")
  public void assetWithIdHasNot(String assetId, String children, String field) {
    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "\"ids\": [\"" +
                    assetId +
                    "\"],\n" +
                    "  \"includeChildren\": " +
                    children +
                    ",\n" +
                    "  \"pageNumber\": 0,\n" +
                    "  \"pageSize\": 20\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/tree")
            .then()
            .extract()
            .response();

    assertEquals("Response failed", 200, response.statusCode());

    String rsBody = response.getBody().asString();
    assertFalse("Asset have field" + field , rsBody.contains(field));

  }
    @Then("^Search tree with namePattern \"([^\"]*)\" and children \"([^\"]*)\" is result searchId \"([^\"]*)\" childrenId \"([^\"]*)\"")
    public void searchTree (String namePattern, String children, String searchId, String childrenId) {


        Response response = given()
                .contentType("application/json")
                .body("{\n" +
                        "  \"namePattern\": \"" +
                        namePattern +
                        "\",\n" +
                        "  \"includeChildren\": " +
                        children +
                        "\n" +
                        "}")
                .when()
                .post(envProperties.getProperty("asset.host") + "search/tree")
                .then()
                .extract()
                .response();

        assertEquals("Response failed", 200, response.statusCode());

        String resultsId = response.jsonPath().get("id[0]");
        List resultChildrenIdsCollections = response.path("children.id");
        Set resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections);
        log("resultId: " + resultsId);
        log("resultChildrenId: " + resultChildrenIds);

        boolean compare;
        compare = resultsId.equals(searchId)&&resultChildrenIds.contains(childrenId);
        System.out.println("Exception result: " + compare);
        assertEquals("Search is failed", true, compare);
    }

  @Then("^Search tree with assetId \"([^\"]*)\" is result searchId \"([^\"]*)\"")
  public void assetWrongId(String assetId, String searchId) {
    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\"\n" +
                    "  ]  \n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/tree")
            .then()
            .extract()
            .response();
response.prettyPrint();
    assertEquals("Response failed", 200, response.statusCode());


    String rsBody = response.getBody().asString();
    assertTrue("Asset have't field" + searchId , rsBody.contains(searchId));

  }

  @Then("^Search tree with namePattern \"([^\"]*)\" assetId \"([^\"]*)\" is result searchId \"([^\"]*)\"")
  public void assetWrongName(String namePattern, String assetId, String searchId) {
    Response response = given()
            .contentType("application/json")
            .body("{\n" +
                    "  \"namePattern\": \"" +
                    namePattern +
                    "\",\n" +
                    "  \"ids\": [\n" +
                    "    \"" +
                    assetId +
                    "\"\n" +
                    "  ],\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("asset.host") + "search/tree")
            .then()
            .extract()
            .response();
    response.prettyPrint();
    assertEquals("Response failed", 200, response.statusCode());


    String rsBody = response.getBody().asString();
    assertTrue("Asset have't field" + searchId , rsBody.contains(searchId));

  }

}
