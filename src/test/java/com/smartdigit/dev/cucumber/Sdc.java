package com.smartdigit.dev.cucumber;

import com.smartdigit.dev.generator.Rest;
import com.smartdigit.dev.generator.template.FreemarkerEngine;
import com.smartdigit.dev.utils.CollectionUtils;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import freemarker.template.TemplateException;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.smartdigit.dev.generator.Common.log;
import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

public class Sdc {

  private final static Properties envProperties = readEnvProperties();


  @When("^Edit rule with name \"([^\"]*)\"(?:,|\\.Set) description \"([^\"]*)\", inputType \"([^\"]*)\", outputType \"([^\"]*)\" and expression \"([^\"]*)\"$")
  public void editRule(String name
          , String description
          , String inputType
          , String outputType
          , String expression) throws IOException, TemplateException {


    Response responseId = Rest.get("sdc.host", "processor/meta/?name=" + name, Hooks.getUaaTokenSdc());

    int ruleId = responseId.path("data.content[0].id");

    log("RuleId:" + ruleId);

    FreemarkerEngine fe = new FreemarkerEngine();
    String script = fe.createRuleScript(expression);

    Response response = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("{\"id\":\""+ruleId+"\",\"name\":\""
                    +name
                    +"\",\"templateId\":1,\"script\":\""
                    +script
                    +"\",\"description\":\""
                    +description
                    +"\",\"inputType\":\""
                    +inputType
                    +"\",\"outputType\":\""
                    +outputType
                    +"\"}")
            .when()
            .post(envProperties.getProperty("sdc.host") + "processor/meta/")
            .then()
            .extract()
            .response();

    response.prettyPrint();

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);

    log("Rule ["+name+"] are edited.");
  }

  @When("^Create rule with name \"([^\"]*)\"(?:,|\\.Set) description \"([^\"]*)\", inputType \"([^\"]*)\", outputType \"([^\"]*)\" and expression \"([^\"]*)\"$")
  public void createRule(String name
          , String description
          , String inputType
          , String outputType
          , String expression) throws IOException, TemplateException {

    FreemarkerEngine fe = new FreemarkerEngine();
    String script = fe.createRuleScript(expression);

    Response response = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("{\"name\":\""
                    +name
                    +"\",\"templateId\":1,\"script\":\""
                    +script
                    +"\",\"description\":\""
                    +description
                    +"\",\"inputType\":\""
                    +inputType
                    +"\",\"outputType\":\""
                    +outputType
                    +"\"}")
            .when()
            .post(envProperties.getProperty("sdc.host") + "processor/meta/")
            .then()
            .extract()
            .response();

    response.prettyPrint();

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);

    log("Rule ["+name+"] are created.");
  }


  @When("^Delete rule \"([^\"]*)\" from SDC$")
  public void deleteRule(String name) {

    Response responseId = Rest.get("sdc.host", "processor/meta/?name=" + name, Hooks.getUaaTokenSdc());

    int ruleId = responseId.path("data.content[0].id");

    log("RuleId:" + ruleId);

    Response response = given()
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .when()
            .delete(envProperties.getProperty("sdc.host") + "processor/meta/" + ruleId)
            .then()
            .extract()
            .response();

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);

    String rsRuleBody = response.getBody().asString();
    assertTrue("An error occur while deleting rule", rsRuleBody.contains("true"));

    log("Rule ["+name+"] are deleted.");
  }

  @When("Bind tag \"([^\"]*)\" to rule \"([^\"]*)\"")
  public void bindTagToRule(String tag, String ruleName) {
    Response response = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .when()
            .post(envProperties.getProperty("sdc.host") + "tag-mapping/"+tag+"/"+ruleName)
            .then()
            .extract()
            .response();

    response.prettyPrint();

    log("Tag ["+tag+"] and rule ["+ruleName+"] are binded.");
  }

  @When("Delete binding tag \"([^\"]*)\" and rule \"([^\"]*)\"")
  public void deleteBindingTagAndRule(String tag, String ruleName) {

    Response responseId = Rest.get("sdc.host", "tag-mapping/?tag=" + tag, Hooks.getUaaTokenSdc());

    int linkId = responseId.path("data.content[0].id");

    log("Received link id [" + linkId + "]");

    Response response = given()
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("[ \""+ruleName+"\"]")
            .when()
            .delete(envProperties.getProperty("sdc.host") + "tag-mapping/" + linkId)
            .then()
            .extract()
            .response();

    assertEquals("Response is failed",200, response.statusCode());

    log("Link of tag ["+tag+"] and rule ["+ruleName+"] is deleted.");
  }

  @Given("^There is no rule with name \"([^\"]*)\" in SDC$")
  public void checkNoRule(String name) {
    Response response = Rest.get("sdc.host", "processor/meta/?name=" + name, Hooks.getUaaTokenSdc());

    int rsStatus = response.statusCode();
    assertEquals("Response is failed", 200, rsStatus);

    String rsRuleName = response.getBody().asString();
    assertTrue("Rule " + name + " exists in SDC", !rsRuleName.contains(name));
    log("Rule [" + name + "] doesn't exist in SDC");
  }

  @Then("^Rule \"([^\"]*)\" exists in SDC$")
  public void checkRuleExists(String name) {
    Response response = Rest.get("sdc.host", "processor/meta/?name=" + name, Hooks.getUaaTokenSdc());

    int rsStatus = response.statusCode();
    assertEquals("Response is failed", 200, rsStatus);

    String rsRuleBody = response.getBody().asString();
    assertTrue("Rule " + name + " doesn't exist in SDC", rsRuleBody.contains(name));

    String rsRuleName = response.path("data.content.name[0]");
    assertEquals(name, rsRuleName);

    log("Rule [" + name + "] exists in SDC");
  }

  //==================================
  //Example:
  //Rule with name "MultiplyOn1001" contains "[description|script|inputType|outputType]" : "Edit100"
  //==================================
  @Then("Rule with name \"([^\"]*)\" contains \"([^\"]*)\" : \"([^\"]*)\"")
  public void checkRuleContainsDescription(String ruleName, String ruleAttribute, String ruleAttributeValue) {
    Response response = Rest.get("sdc.host", "processor/meta/?name=" + ruleName, Hooks.getUaaTokenSdc());

    assertEquals("Response is failed", 200, response.statusCode());

    String responseAttrValue = response.path("data.content[0]."+ruleAttribute).toString();

    assertTrue("There is no " + ruleAttribute + "=" + ruleAttributeValue + " in rule " + ruleName, responseAttrValue.contains(ruleAttributeValue));

    log("In rule [" + ruleName + "] attribute [" + ruleAttribute + "] = [" + ruleAttributeValue + "]");

  }

  @Then("Tag \"([^\"]*)\" and rule \"([^\"]*)\" are binded \"(true|false)\"")
  public void tagAndRuleAreBinded(String tag, String ruleName, String isExist) {
    Response response = Rest.get("sdc.host", "tag-mapping/?tag=" + tag, Hooks.getUaaTokenSdc());

    int rsStatus = response.statusCode();
    assertEquals("Response is failed", 200, rsStatus);

    if (Boolean.valueOf(isExist)) {
      assertTrue("Tag [" + tag + "] is not binded with rule [" + ruleName + "]", response.getBody().asString().contains(ruleName));
      log("Tag [" + tag + "] is binded with rule [" + ruleName + "]");
    } else {
      assertFalse("Tag [" + tag + "] is binded with rule [" + ruleName + "]", response.getBody().asString().contains(ruleName));
      log("Tag [" + tag + "] is not binded with rule [" + ruleName + "]");
    }

  }

  //==================================
  //TEST SORTING BELOW
  //==================================

  @Given("^Create test_sorting templateId with name \"([^\"]*)\" and createdBy \"([^\"]*)\"")
  public void createTestTemplateId(String name, String createdBy) throws IOException, TemplateException {


    Response response = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("{\n" +
                    "  \"body\": \"string\",  \n" +
                    "  \"createdBy\": \"" +
                    createdBy +
                    "\",\n" +
                    "  \"language\": \"groovy\",\n" +
                    "  \"name\": \"" +
                    name +
                    "\" \n" +
                    "}")
            .when()
            .post(envProperties.getProperty("sdc.host") + "processor/template/")
            .then()
            .extract()
            .response();

//    response.prettyPrint();
    int testTemplateId = response.path("data.id");

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);
    log("TemplateId ["+testTemplateId+"] are created.");
    //SAVE NEW RULE IN THAT TEMPLATE
    Response responseSave = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("{\n" +
                    "  \"createdBy\": \"" +
                    createdBy +
                    "\",\n" +
                    "  \"description\": \"string\", \n" +
                    "  \"inputType\": \"LONG\",\n" +
                    "  \"name\": \"" +
                    name +
                    "\",\n" +
                    "  \"outputType\": \"LONG\",\n" +
                    "  \"script\": \"string\",\n" +
                    "  \"templateId\":" +
                    testTemplateId +
                    "\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("sdc.host") + "processor/meta/")
            .then()
            .extract()
            .response();
//    response.prettyPrint();
    int rsStatusSave = responseSave.statusCode();
    assertEquals("Response is failed",200, rsStatusSave);
  }


  @Given("^Create test_sorting rule with name \"([^\"]*)\", description \"([^\"]*)\", inputType \"([^\"]*)\", outputType \"([^\"]*)\", templateId \"([^\"]*)\", script \"([^\"]*)\", createdBy \"([^\"]*)\", updatedBy \"([^\"]*)\"")
  public void createTestSortingRule(String name
          , String description
          , String inputType
          , String outputType
          , String templateId
          , String script
          , String createdBy
          , String updatedBy) throws IOException, TemplateException {

//    FreemarkerEngine fe = new FreemarkerEngine();
//    String script = fe.createRuleScript(expression);

    Response response = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("{  \n" +
                    "  \"createdBy\": \"" +
                    createdBy +
                    "\",\n" +
                    "  \"description\": \"" +
                    description +
                    "\",\n" +
                    "  \"inputType\": \"" +
                    inputType +
                    "\",\n" +
                    "  \"name\": \"" +
                    name +
                    "\",\n" +
                    "  \"outputType\": \"" +
                    outputType +
                    "\",\n" +
                    "  \"script\": \"" +
                    script +
                    "\",\n" +
                    "  \"templateId\": " +
                    templateId +
                    ",\n" +
                    "  \"updatedBy\": \"" +
                    updatedBy +
                    "\"\n" +
                    "}")
            .when()
            .post(envProperties.getProperty("sdc.host") + "processor/meta/")
            .then()
            .extract()
            .response();

//    response.prettyPrint();

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);

    log("Rule ["+name+"] are created.");
  }
  @Given("^Update test_sorting rule with name \"([^\"]*)\", description \"([^\"]*)\", inputType \"([^\"]*)\", outputType \"([^\"]*)\", templateId \"([^\"]*)\", script \"([^\"]*)\", updatedBy \"([^\"]*)\"")
  public void createTestSortingRule(String name
          , String description
          , String inputType
          , String outputType
          , String templateId
          , String script
          , String updatedBy) throws IOException, TemplateException {

    Response responseId = Rest.get("sdc.host", "processor/meta/?name=" + name, Hooks.getUaaTokenSdc());

    assertEquals("Response is failed", 200, responseId.statusCode());

    String ruleId = responseId.path("data.content[0].id").toString();
//    System.out.println("!!!!!!!!!!!!!!ruleId: " + ruleId);

    Response response = given()
            .contentType("accept: */*")
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .body("{\n" +
                    "  \"updatedBy\": \"" +
                    updatedBy +
                    "\",\n" +
                    "  \"description\": \"" +
                    description +
                    "\",\n" +
                    "  \"id\": \"" +
                    ruleId +
                    "\",\n" +
                    "  \"inputType\": \"" +
                    inputType +
                    "\",\n" +
                    "  \"name\": \"" +
                    name +
                    "\",\n" +
                    "  \"outputType\": \"" +
                    outputType +
                    "\",\n" +
                    "  \"script\": \"" +
                    script +
                    "\",\n" +
                    "  \"templateId\": " +
                    templateId +
                    " \n" +
                    "}")
            .when()
            .post(envProperties.getProperty("sdc.host") + "processor/meta/")
            .then()
            .extract()
            .response();

//    response.prettyPrint();

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);

    log("Rule ["+name+"] are created.");
  }

  @When("^Sorting rule by name \"([^\"]*)\" and createdBy \"([^\"]*)\" with param \"([^\"]*)\" and result is$")
  public void sorting(String name, String createdBy, String param, List<String> is) throws IOException, TemplateException {

    Response response = Rest.get("sdc.host", "processor/meta/?createdBy="+createdBy+"&sort="+name+":"+param, Hooks.getUaaTokenSdc());
//    response.prettyPrint();
    log("!!SORTING CONTENT:\n" + response.jsonPath().getList("data.content."+name).stream().map(o -> (String) o).collect(Collectors.toList()));
    List<String> sortedList = response.jsonPath().getList("data.content."+name).stream().map(o -> (String) o).collect(Collectors.toList());


    int rsStatus = response.statusCode();
    assertEquals("Response is failed", 200, rsStatus);

//    List resultChildrenIdsCollections = response.path("data.content."+name);
//    System.out.println("Exception result: " + resultChildrenIdsCollections);
//    String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
//    log("resultChildrenId: " + resultChildrenIds);


    boolean compare;
//    compare = resultChildrenIds.equals(is);
    compare = sortedList.equals(is);
    System.out.println("Exception result: " + compare);
    log("Sort with " +name+ " successful.");
    assertEquals("Sorting is failed",true, compare);
  }

  @When("^Sorting rule by name \"([^\"]*)\" and updatedBy \"([^\"]*)\" with param \"([^\"]*)\" and result is$")
  public void sortingUpdatedBy(String name, String updatedBy, String param, List<String> is) throws IOException, TemplateException {

    Response response = Rest.get("sdc.host", "processor/meta/?updatedBy="+updatedBy+"&sort="+name+":"+param, Hooks.getUaaTokenSdc());
//    response.prettyPrint();
    log("!!SORTING CONTENT:\n" + response.jsonPath().getList("data.content."+name).stream().map(o -> (String) o).collect(Collectors.toList()));
    List<String> sortedList = response.jsonPath().getList("data.content."+name).stream().map(o -> (String) o).collect(Collectors.toList());


    int rsStatus = response.statusCode();
    assertEquals("Response is failed", 200, rsStatus);

//    List resultChildrenIdsCollections = response.path("data.content."+name);
//    System.out.println("Exception result: " + resultChildrenIdsCollections);
//    String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
//    log("resultChildrenId: " + resultChildrenIds);


    boolean compare;
//    compare = resultChildrenIds.equals(is);
    compare = sortedList.equals(is);
    System.out.println("Exception result: " + compare);
    log("Sort with " +name+ " successful.");
    assertEquals("Sorting is failed",true, compare);
  }

  @When("^Sorting rule by name \"([^\"]*)\" and createdBy \"([^\"]*)\" with param \"([^\"]*)\" and result tags list is$")
  public void sortingWithTagsResult (String name, String createdBy, String param, List<String> is) throws IOException, TemplateException {

    Response response = Rest.get("sdc.host", "processor/meta/?createdBy="+createdBy+"&sort="+name+":"+param, Hooks.getUaaTokenSdc());
//    response.prettyPrint();
    log("!!SORTING CONTENT:\n" + response.jsonPath().getList("data.content.name").stream().map(o -> (String) o).collect(Collectors.toList()));
    List<String> sortedList = response.jsonPath().getList("data.content.name").stream().map(o -> (String) o).collect(Collectors.toList());

    int rsStatus = response.statusCode();
    assertEquals("Response is failed", 200, rsStatus);

    boolean compare;
    compare = sortedList.equals(is);
    System.out.println("Exception result: " + compare);
    log("Sort with " +name+ " successful.");
    assertEquals("Sorting is failed",true, compare);
  }



  @When("^Delete test_templateId with name \"([^\"]*)\" from SDC")
  public void deleteTestTemplateId(String name) {
    Response responseId = Rest.get("sdc.host", "processor/meta/?name="+name, Hooks.getUaaTokenSdc());
    String templateId = responseId.path("data.content[0].templateId").toString();

//    Response responseId = Rest.get("sdc.host", "processor/template/", Hooks.getUaaTokenSdc());
//      responseId.prettyPrint();
//      log("!!SORTING CONTENT:\n" + responseId.jsonPath().getList("data.name").stream().map(o -> (String) o).collect(Collectors.toList()));
//    String testTemplateId = responseId.jsonPath().getString("data.name== \"SDC_TEMPLATE3\".id");
//    List<String> testTemplateId = responseId.jsonPath().getList("data.id").stream().map(o -> (String) o).collect(Collectors.toList());
//      List resultChildrenIdsCollections = responseId.path("data.name");
//      String resultChildrenIds = CollectionUtils.flattenToSet(resultChildrenIdsCollections).toString();
//      System.out.println("XXXXXXXX: " + resultChildrenIds);
//    System.out.println("11111111: " + testTemplateId);
    Response response = given()
            .contentType("application/json")
            .queryParam("access_token", Hooks.getUaaTokenSdc())
            .when()
            .delete(envProperties.getProperty("sdc.host") + "processor/template/" + templateId)
            .then()
            .extract()
            .response();

    int rsStatus = response.statusCode();
    assertEquals("Response is failed",200, rsStatus);
////
////    String rsRuleBody = response.getBody().asString();
////    assertTrue("An error occur while deleting rule", rsRuleBody.contains("true"));
//
    log("Rule ["+templateId+"] are deleted.");
  }

}

