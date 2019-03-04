package com.smartdigit.dev.generator.template;

import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;

import org.junit.Test;

public class FreemarkerTemplateEngineTest {

  @Test
  public void renderingSample() throws Exception {
    final FreemarkerTemplateEngine engine = new FreemarkerTemplateEngine();


    final String doc = engine.renderDoc(
        engine.getTemplateCtx(readEnvProperties().getProperty("template.name"), new MsgRandomDataModel())
    );

    System.out.println("doc = " + doc);
  }

}
