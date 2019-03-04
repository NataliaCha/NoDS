package com.smartdigit.dev.generator.template;

import freemarker.template.Template;

public class TemplateCtx {

  public final String templateName;
  public final Template template;
  public final Object dataModel;

  public TemplateCtx(String templateName, Template template, Object dataModel) {
    this.templateName = templateName;
    this.template = template;
    this.dataModel = dataModel;
  }

  @Override
  public String toString() {
    return "TemplateCtx{" +
        "template=" + template +
        ", dataModel=" + dataModel +
        '}';
  }
}
