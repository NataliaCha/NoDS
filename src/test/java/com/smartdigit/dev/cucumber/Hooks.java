package com.smartdigit.dev.cucumber;

import static com.smartdigit.dev.generator.Common.log;
import static com.smartdigit.dev.generator.ConfigUtils.readEnvProperties;

import com.smartdigit.dev.generator.Rest;
import com.smartdigit.dev.generator.Uaa;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import java.util.Properties;

public class Hooks {

    private static String uaaToken;
    private static String uaaTokenMrj;
    private static String uaaTokenSdc;
    private static String uaaTokenAsset;
    private static String uaaTokenMds;

    private final static Properties envProperties = readEnvProperties();

    @Before(value = "@mapped")
    public void prepareData() {

        uaaToken = Uaa.generateUaaToken(envProperties.getProperty("uaa.url")
                , envProperties.getProperty("uaa.client.ts")
                , envProperties.getProperty("uaa.secret.ts"));
        log("UAA token TS:" + uaaToken);

    }

    @Before(value = "@unmapped or @mrj")
    public void prepareDataUnmapped() {

        uaaTokenMrj = Uaa.generateUaaToken(envProperties.getProperty("uaa.url.mrj")
                , envProperties.getProperty("uaa.client.mrj")
                , envProperties.getProperty("uaa.secret.mrj"));

        log("UAA token MRJ:" + uaaTokenMrj);

    }


    @Before(value = "@sdc or @mapped or @mapper")
    public void prepareDataSdc() {

        uaaTokenSdc = Uaa.generateUaaToken(envProperties.getProperty("uaa.url.mrj")
                , envProperties.getProperty("uaa.client.sdc")
                , envProperties.getProperty("uaa.secret.sdc"));

        log("UAA token SDC:" + uaaTokenSdc);

    }

    @Before(value = "@asset")
    public void prepareDataAsset() {

        uaaTokenAsset = Uaa.generateUaaToken(envProperties.getProperty("uaa.url.mrj")
                , envProperties.getProperty("uaa.client.asset")
                , envProperties.getProperty("uaa.secret.asset"));

        log("UAA token Asset:" + uaaTokenAsset);

    }

    @Before(value = "@mapped_data_pg")
    public void prepareUaaTokenMds() {

        uaaTokenMds = Uaa.generateUaaToken(envProperties.getProperty("uaa.url.mrj")
                , envProperties.getProperty("uaa.client.mds")
                , envProperties.getProperty("uaa.secret.mds"));
        log("UAA token MDS:" + uaaTokenMds);

    }

    @After
    public void getScenarioInfo(Scenario scenario) {
        log(System.getProperty("env") + ":" + scenario.getName()+":"+scenario.getStatus().toString());
        Rest.postToQa(scenario.getName(), System.getProperty("env"), scenario.getStatus().toString(), "");
    }

    public static String getUaaToken(){
        return uaaToken;
    }

    public static String getUaaTokenMrj(){
        return uaaTokenMrj;
    }

    public static String getUaaTokenSdc(){
        return uaaTokenSdc;
    }

    public static String getUaaTokenAsset(){
        return uaaTokenAsset;
    }

    public static String getUaaTokenMds(){return uaaTokenMds;}
}