package com.smartdigit.dev.generator.template;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FreemarkerTemplateEngine {

  public static final Version VERSION = Configuration.VERSION_2_3_28;

  private final Configuration cfg;

  public FreemarkerTemplateEngine() throws IOException {
    final Properties env = readEnvProperties();
    cfg = new Configuration(VERSION);
    cfg.setDirectoryForTemplateLoading(new File(env.getProperty("template.path")));
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(true);
    cfg.setWrapUncheckedExceptions(true);
  }

  public TemplateCtx getTemplateCtx(String templateFileName, Object dataModel) throws IOException {
    return new TemplateCtx(templateFileName, cfg.getTemplate(templateFileName), dataModel);
  }

  public String renderDoc(TemplateCtx templateCtx) throws IOException, TemplateException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
    try (Writer out = new OutputStreamWriter(bos)){
      templateCtx.template.process(templateCtx.dataModel, out);
      return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }
  }

}
