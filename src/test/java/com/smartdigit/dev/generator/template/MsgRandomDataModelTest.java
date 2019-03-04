package com.smartdigit.dev.generator.template;

import com.smartdigit.dev.generator.template.MsgRandomDataModel.ValueContext;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class MsgRandomDataModelTest {

  @Test
  public void matchIntRange() {
    final ValueContext valueContext = new MsgRandomDataModel().matchIntRange("intRange12_15");
    System.out.println("valueContext = " + valueContext);
    Assert.assertTrue(Integer.parseInt(valueContext.renderRandom()) < 15);
  }

  private final NumberFormat doubleFormat = new DecimalFormat("##.0");

  @Test
  public void matchDoubleRange() throws ParseException {
    final ValueContext valueContext = new MsgRandomDataModel().matchDoubleRange("doubleRange11_42");
    System.out.println("valueContext = " + valueContext);
    Assert.assertTrue(doubleFormat.parse(valueContext.renderRandom()).doubleValue() < 42.0);
  }
}
