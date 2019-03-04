package com.smartdigit.dev.generator.template;

import com.smartdigit.dev.generator.ConfigUtils;
import freemarker.template.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;

public class FreemarkerEngine {

  public static final Version VERSION = Configuration.VERSION_2_3_28;

  private final Configuration cfg;

  private final static Properties envProperties = readEnvProperties();



  public FreemarkerEngine() throws IOException {
    final Properties env = readEnvProperties();
    cfg = new Configuration(VERSION);
    cfg.setDirectoryForTemplateLoading(new File(env.getProperty("template.path")));
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(true);
    cfg.setWrapUncheckedExceptions(true);
  }

  private String renderDoc(Template templ, Map<String,String> dataModel) throws IOException, TemplateException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
    try (Writer out = new OutputStreamWriter(bos)){
      templ.process(dataModel, out);
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }
  }

  //Fill DP with values
  public String createDataPoint(String tagName, String val, String unit, String qual) throws IOException, TemplateException {

    /* Create a data-model */
    Map root = new HashMap();
    root.put("name", tagName);
    root.put("ts", ConfigUtils.getTS());
    root.put("val", val);
    root.put("msgId", ConfigUtils.getMessageId());
    root.put("unit", unit);
    root.put("qual", qual);

    /* Get the template (uses cache internally) */
    Template temp = cfg.getTemplate(envProperties.getProperty("template.name"));

    return renderDoc(temp,root);

  }

  public String createRuleScript(String expression) throws IOException, TemplateException {

    Map root = new HashMap();
    root.put("expr", expression);

    Template temp = cfg.getTemplate(envProperties.getProperty("sdc.template.script"));

    return renderDoc(temp,root);

  }

  public String createAsset(String assetId) throws IOException, TemplateException {

    Map root = new HashMap();
    root.put("assetId", assetId);

    Template temp = cfg.getTemplate(envProperties.getProperty("asset.create.template"));

    return renderDoc(temp,root);

  }

  public String updateAsset(Map<String, String> argAsset) throws IOException, TemplateException {

    Template temp = cfg.getTemplate(envProperties.getProperty("asset.update.template"));

    return renderDoc(temp,argAsset);

  }

}
