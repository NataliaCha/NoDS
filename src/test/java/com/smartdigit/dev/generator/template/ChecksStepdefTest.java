package com.smartdigit.dev.generator.template;

import com.smartdigit.dev.cucumber.ChecksStepdef;
import org.junit.Assert;
import org.junit.Test;

public class ChecksStepdefTest {

  @Test
  public void checkDataPointInPostgressTest() {

    ChecksStepdef sd = new ChecksStepdef();
    sd.checkDataPointInPostgress("Root.PDH.112 Oleflex.112LI003A","bar","444.44");
  }

}
