package com.smartdigit.dev.generator.template;

import org.junit.Assert;
import org.junit.Test;

public class FreemarkerEngineTest {

  @Test
  public void renderingSample() throws Exception {

    FreemarkerEngine fe = new FreemarkerEngine();
    System.out.println(fe.createDataPoint("SENSOR.TEMPERATURE.2", "212", "meter", "2"));

//    long ts = System.currentTimeMillis() - 3600 * 24 * 1000;
//    System.out.println("ts:" + ts);


    Assert.assertTrue(true);
  }

}
