package com.smartdigit.dev.generator.eventhub;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;
import static com.smartdigit.dev.generator.MetricsRepo.metrics;

import com.codahale.metrics.Counter;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.PublishConfiguration;
import com.ge.predix.eventhub.configuration.SubscribeConfiguration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventHubFacade {

  private static final Logger log = Logger.getLogger(EventHubFacade.class.getName());

  private final AtomicLong idSeq = new AtomicLong(1);

  private final Client eventHubClient;
  private final Counter counter = metrics.counter(EventHubFacade.class.getName());

  public EventHubFacade() throws EventHubClientException {
    final Properties env = readEnvProperties();
    final EventHubConfiguration config = new EventHubConfiguration.Builder()
        .host(env.getProperty("eventHub.host"))
        .port(Integer.parseInt(env.getProperty("eventHub.port")))
        .clientID(env.getProperty("eventHub.clientId"))
        .clientSecret(env.getProperty("eventHub.secret"))
        .authURL(env.getProperty("eventHub.authUrl"))
        .zoneID(env.getProperty("eventHub.zoneId"))
        .publishConfiguration(new PublishConfiguration.Builder().build())
        .subscribeConfiguration(new SubscribeConfiguration.Builder()
            .subscriberName(env.getProperty("eventHub.subscriber")).build())
        .build();

    eventHubClient = new Client(config);
    eventHubClient.registerPublishCallback(new PubCallback());
    eventHubClient.subscribe(new SubscribeLoggingCallback());
  }

  public void sendMessage(String msg){
    try {
      eventHubClient.addMessage(Long.toString(idSeq.getAndIncrement()), msg, null);
      counter.inc();
    } catch (EventHubClientException e) {
      log.log(Level.SEVERE, "Can't send message", e);
    }
  }

  public void flush(){
    try {
      eventHubClient.flush();
    } catch (EventHubClientException e) {
      log.log(Level.SEVERE, "Can't flush!", e);
    }
  }

  public void close(){
    try {
      eventHubClient.close();
    } catch (Exception e) {
      log.log(Level.WARNING, "Can't close client", e);
    }
  }

  public static void main(String[] args) throws EventHubClientException, InterruptedException {
    final EventHubFacade facade = new EventHubFacade();
    facade.sendMessage("Hello");
    facade.flush();
    Thread.sleep(100_000);
    facade.close();
  }

}
