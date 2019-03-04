package com.smartdigit.dev.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Common {

    public static final Logger LOG = LoggerFactory.getLogger(Common.class);

    public static void writeToFile(String fileName, List<String> lines) throws IOException {

        Path file = Paths.get(fileName);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    public static void log(String message) {
          LOG.info(message);
        //Allure.LIFECYCLE.fire(new AddParameterEvent(message, value));
    }
}
