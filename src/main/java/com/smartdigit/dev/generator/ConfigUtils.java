package com.smartdigit.dev.generator;

import java.io.FileReader;
import java.util.Properties;
import java.util.UUID;

public class ConfigUtils {

  private static long ts = System.currentTimeMillis();

  public static String messageId = null;

  public ConfigUtils() {
  }

  public static Properties readEnvProperties() {
    final Properties envProperties = new Properties();

    String propFileName = "env.properties";
    String envProperty = System.getProperty("env");
    if(envProperty != null) {
      if (envProperty.equals("dev")) propFileName = "env-dev.properties";
      if (envProperty.equals("int")) propFileName = "env-int.properties";
      if (envProperty.equals("qa")) propFileName = "env-qa.properties";
    }
    System.out.println("Environment:"+propFileName);
    try(FileReader fr = new FileReader(propFileName)){
      envProperties.load(fr);
      return envProperties;
    } catch (Exception e) {
      throw new IllegalStateException("Can't load env.properties", e);
    }
  }

  public static String getMessageId() {
    UUID uid = UUID.randomUUID();
    messageId = uid.toString();
    return uid.toString();
  }

  public static String getTS(){
    return Long.toString(ts);
  }

}
