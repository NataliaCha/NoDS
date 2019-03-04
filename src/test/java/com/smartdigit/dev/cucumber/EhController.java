package com.smartdigit.dev.cucumber;

import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.SubscribeConfiguration;
import com.smartdigit.dev.generator.eventhub.EventHubSubscriber;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.restassured.path.json.JsonPath;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import static com.smartdigit.dev.generator.Common.log;
import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static org.junit.Assert.assertEquals;

public class EhController {

  public static String valTs;
  public static String valMessage;

  @Given("^There are (\\d+) notification in SDC EH$")
  public void listenEHofSDC(int notifCount) throws EventHubClientException, InterruptedException {
    final Properties env = readEnvProperties();
    final EventHubConfiguration config = new EventHubConfiguration.Builder()
            .host(env.getProperty("eventHub.host"))
            .port(Integer.parseInt(env.getProperty("eventHub.port")))
            .authURL(env.getProperty("eventHub.authUrl"))
            .clientID(env.getProperty("eventHub.clientId"))
            .clientID(env.getProperty("eventHub.sdc.subscriber.clientId"))
            .clientSecret(env.getProperty("eventHub.sdc.subscriber.secret"))
            .zoneID(env.getProperty("eventHub.sdc.zoneId"))
            .subscribeConfiguration(new SubscribeConfiguration.Builder()
                    .subscriberName(env.getProperty("eventHub.sdc.subscriber")).build())
            .build();

    final EventHubSubscriber facade = new EventHubSubscriber(config);
    facade.ehSubscribe();
    Thread.sleep(3_000);
    facade.close();

    assertEquals("Amount of messages in EH ["+facade.msgCount()+"] not like as expected ["+notifCount+"].",notifCount, facade.msgCount());

    log("Amount of messages in EH ["+facade.msgCount()+"] like as expected ["+notifCount+"].");

    List<String> list = facade.listOfMessages();

    for(int i=0;i<list.size();i++)
      assertEquals("The body of message in SDC EH ["+list.get(i)+"] is not notify.","notify", list.get(i));

  }


  @Then("Message with such \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" is in EH mapped")
  public void listenEHofMappedLoader(String tag, String qual, String expVal) throws EventHubClientException, InterruptedException {
    boolean isResult = false;
    final Properties env = readEnvProperties();
    final EventHubConfiguration config = new EventHubConfiguration.Builder()
            .host(env.getProperty("eventHub.host"))
            .port(Integer.parseInt(env.getProperty("eventHub.port")))
            .authURL(env.getProperty("eventHub.authUrl"))
            .clientID(env.getProperty("eventHub.clientId"))
            .clientID(env.getProperty("eventHub.mapped.subscriber.clientId"))
            .clientSecret(env.getProperty("eventHub.mapped.subscriber.secret"))
            .zoneID(env.getProperty("eventHub.mapped.zoneId"))
            .subscribeConfiguration(new SubscribeConfiguration.Builder()
                    .subscriberName(env.getProperty("eventHub.mapped.subscriber")).build())
            .build();

    final EventHubSubscriber facade = new EventHubSubscriber(config);
    facade.ehSubscribe();
    Thread.sleep(6_000);
    facade.close();

    log("Amount of messages in EH [" + facade.msgCount() + "]");

    List<String> list = facade.listOfMessages();

    for (int i = 0; i < list.size(); i++) {
      String dpTag = JsonPath.with(list.get(i)).get("body[0].tag");
      String dpTs = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].ts");
      String dpVal = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].val");
      String dpQual = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].qual");
      String dpMessageId = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].messageId");
      String dpFunc = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].mFunc");

      if (dpTag.contains(tag) && dpQual.contains(qual) && dpVal.contains(expVal)) {
        isResult = true;
        log("DP [" + dpTag + ":" + dpTs + ":" + dpVal + ":" + dpQual + ":" + dpMessageId + ":" + dpFunc + "]");
      }
    }

    assertTrue("No such data point [" + tag + "] in EH", isResult);
  }

  @Then("Message with such \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" is in EH unmapped")
  public void listenEHofUnmappedLoader(String tag, String qual, String expVal) throws EventHubClientException, InterruptedException {
    boolean isResult = false;
    final Properties env = readEnvProperties();
    final EventHubConfiguration config = new EventHubConfiguration.Builder()
            .host(env.getProperty("eventHub.host"))
            .port(Integer.parseInt(env.getProperty("eventHub.port")))
            .authURL(env.getProperty("eventHub.authUrl"))
            .clientID(env.getProperty("eventHub.clientId"))
            .clientID(env.getProperty("eventHub.unmapped.subscriber.clientId"))
            .clientSecret(env.getProperty("eventHub.unmapped.subscriber.secret"))
            .zoneID(env.getProperty("eventHub.unmapped.zoneId"))
            .subscribeConfiguration(new SubscribeConfiguration.Builder()
                    .subscriberName(env.getProperty("eventHub.unmapped.subscriber")).build())
            .build();

    final EventHubSubscriber facade = new EventHubSubscriber(config);
    facade.ehSubscribe();
    Thread.sleep(6_000);
    facade.close();

    log("Amount of messages in EH ["+facade.msgCount()+"]");

    List<String> list = facade.listOfMessages();

    for (int i = 0; i < list.size(); i++) {
      String dpTag = JsonPath.with(list.get(i)).get("body[0].tag");
      String dpTs = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].ts");
      String dpVal = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].val");
      String dpQual = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].qual");
      String dpMessageId = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].messageId");
      String dpMsg = JsonPath.with(list.get(i)).get("body[0].dataPoints[0].eMsg");

      if (dpTag.contains(tag) && dpQual.contains(qual) && dpVal.contains(expVal)) {
        isResult = true;
        log("DP [" + dpTag + ":" + dpTs + ":" + dpVal + ":" + dpQual + ":" + dpMessageId + ":" + dpMsg + "]");
      }
    }

    assertTrue("No such data point [" + tag + "] in EH", isResult);
  }
}
