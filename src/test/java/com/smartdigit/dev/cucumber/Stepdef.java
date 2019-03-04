package com.smartdigit.dev.cucumber;

import com.smartdigit.dev.generator.EHController;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;

import static com.smartdigit.dev.generator.Common.log;

public class Stepdef {

  private final EHController eh;

  public Stepdef() {
    try {
      eh = new EHController();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @When("^Publish message with \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\"$")
  public void publishMessageWithParameters(String tag, String qual, String value) throws Throwable {
      String unit = "bar";
      eh.pushTask(tag, unit, value, qual);
  }


  @And("^Wait (\\d+) sec$")
  public void wait(int sec) throws InterruptedException {
    log("Wait for ["+sec+"] sec");
    Thread.sleep(sec * 1000);
  }


}
