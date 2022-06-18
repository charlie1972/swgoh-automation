package com.charlie.swgoh.connector;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonConnector {

  private JsonConnector() {}

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static <T> T readObjectFromFile(String fileName, Class<T> clazz) throws Exception {
    Reader reader = new InputStreamReader(
            new BufferedInputStream(
                    new FileInputStream(fileName)
            ),
            StandardCharsets.UTF_8
    );
    return MAPPER.readValue(reader, clazz);
  }

  public static void writeObjectToFile(Object object, String fileName) throws Exception {
    Writer writer = new OutputStreamWriter(
            new BufferedOutputStream(
                    new FileOutputStream(fileName)
            ),
            StandardCharsets.UTF_8
    );
    MAPPER.writerWithDefaultPrettyPrinter().writeValue(writer, object);
  }

}
