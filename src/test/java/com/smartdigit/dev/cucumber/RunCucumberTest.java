package com.smartdigit.dev.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"},
        monochrome = true,
        glue = "com.smartdigit.dev.cucumber",
        features = "src/test/resources/com/smartdigit/dev/features",
        snippets = SnippetType.CAMELCASE,
        tags = {"@mapper"}
)
public class RunCucumberTest {

}

