package com.smartdigit.dev.generator.eventhub;

import com.ge.predix.eventhub.Message;
import com.ge.predix.eventhub.client.Client;

import java.util.logging.Level;
import java.util.logging.Logger;

class SubscribeLoggingCallback implements Client.SubscribeCallback {

  private static final Logger log = Logger.getLogger(PubCallback.class.getName());

  @Override
  public void onMessage(Message m) {
//    log.info("Receiving message: " + m.toString());
    EventHubSubscriber.count++;
    EventHubSubscriber.addMessage(m.getBody().toStringUtf8());
    log.info("BODY: " + m.getBody().toStringUtf8());

  }

  @Override
  public void onFailure(Throwable throwable) {
    log.log(Level.WARNING, "Failure on subscriber", throwable);
  }
}
