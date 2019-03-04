package com.smartdigit.dev.generator.eventhub;

import com.ge.predix.eventhub.Ack;
import com.ge.predix.eventhub.client.Client;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PubCallback implements Client.PublishCallback {

  private static final Logger log = Logger.getLogger(PubCallback.class.getName());

  @Override
  public void onAck(List<Ack> list) {
    if (log.isLoggable(Level.INFO)){
      log.info("Acks received: " + String.join(",",
          convertIterator(list.stream().map(Ack::toString).iterator()))
      );
    }
  }

  @Override
  public void onFailure(Throwable throwable) {
    log.log(Level.SEVERE, "Failure on message send", throwable);
  }

  private Iterable<String> convertIterator(Iterator<String> iterator){
    return () -> iterator;
  }
}
