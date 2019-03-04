package com.smartdigit.dev.generator.eventhub;

import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.SubscribeConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;


public class EventHubSubscriber {

  private static final Logger log = Logger.getLogger(EventHubSubscriber.class.getName());

  private final Client eventHubClient;

  public static int count = 0;

  private static List<String> messageBody = new ArrayList<String>();

  public EventHubSubscriber(EventHubConfiguration config) throws EventHubClientException {
    final Properties env = readEnvProperties();
    final EventHubConfiguration configDef = new EventHubConfiguration.Builder()
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

    eventHubClient = new Client(config);
  }

  public static void addMessage(String message){
    messageBody.add(message);
  }

  public int msgCount(){
    return messageBody.size();
  }

  public List listOfMessages(){
    return messageBody;
  }

  public void close(){
    try {
      eventHubClient.close();
    } catch (Exception e) {
      log.log(Level.WARNING, "Can't close client", e);
    }
  }

  public void ehSubscribe() throws EventHubClientException {
    eventHubClient.subscribe(new SubscribeLoggingCallback());
  }

}
