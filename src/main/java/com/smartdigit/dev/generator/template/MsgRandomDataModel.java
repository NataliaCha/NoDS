package com.smartdigit.dev.generator.template;

import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.WrappingTemplateModel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsgRandomDataModel extends WrappingTemplateModel implements TemplateHashModel {

  private final Pattern doublePattern = Pattern.compile("doubleRange(\\d+)_(\\d+)");
  private final Pattern intPattern = Pattern.compile("intRange(\\d+)_(\\d+)");

  private final NumberFormat doubleFormat =
      new DecimalFormat("##.0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

  private final NumberFormat integerFormat = new DecimalFormat("##");

  private long ts = System.currentTimeMillis() - 3600 * 24 * 1000;

  private long msgId = 1;

  public MsgRandomDataModel() {
    super(new DefaultObjectWrapperBuilder(FreemarkerTemplateEngine.VERSION).build());
  }

  public long getMsgId() {
    return msgId;
  }

  public void setMsgId(long msgId) {
    this.msgId = msgId;
  }

  @Override
  public TemplateModel get(String key) throws TemplateModelException {
    if (key.startsWith("msgId")) return wrap(Long.toString(msgId++));
    if (key.startsWith("ts")) return wrap(Long.toString(ts++));

    ValueContext valueContext = matchDoubleRange(key);
    if (valueContext == null) valueContext = matchIntRange(key);
    if (valueContext == null) return wrap(null);
    return wrap(valueContext.renderRandom());
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  ValueContext matchDoubleRange(String value){
    final Matcher matcher = doublePattern.matcher(value);
    if (matcher.matches()){
      return new ValueContext(doubleFormat,
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2))
      );
    }
    return null;
  }

  ValueContext matchIntRange(String value){
    final Matcher matcher = intPattern.matcher(value);
    if (matcher.matches()){
      return new ValueContext(integerFormat,
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2))
      );
    }
    return null;
  }

  static class ValueContext {
    final NumberFormat numberFormat;

    final int rangeStart;
    final int rangeEnd;

    public ValueContext(NumberFormat numberFormat, int rangeStart, int rangeEnd) {
      this.numberFormat = numberFormat;
      this.rangeStart = rangeStart;
      this.rangeEnd = rangeEnd;
    }

    String renderRandom(){
      return numberFormat.format(Math.random() * rangeEnd);
    }

    @Override
    public String toString() {
      return "ValueContext{" +
          "numberFormat=" + numberFormat +
          ", rangeStart=" + rangeStart +
          ", rangeEnd=" + rangeEnd +
          '}';
    }
  }
}
