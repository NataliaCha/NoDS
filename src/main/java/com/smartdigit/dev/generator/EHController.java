package com.smartdigit.dev.generator;

import com.ge.predix.eventhub.EventHubClientException;
import com.smartdigit.dev.generator.eventhub.EventHubFacade;
import com.smartdigit.dev.generator.template.FreemarkerEngine;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.logging.Logger;

public class EHController {

  private static final Logger log = Logger.getLogger(EHController.class.getName());

  public void pushTask(String tag, String unit, String val, String qual) throws IOException, EventHubClientException, InterruptedException, TemplateException {
    final EventHubFacade facade = new EventHubFacade();
    FreemarkerEngine fe = new FreemarkerEngine();

    String message = fe.createDataPoint(tag, val, unit, qual);

    log.info("Publishing...");
    log.info(message);

    facade.sendMessage(message);
    facade.flush();
    Thread.sleep(1000);
    facade.close();

  }

}
