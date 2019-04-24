package org.pareco.tokenization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class DefaultObjectMapper {
  public static final ObjectMapper get() {
    ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.registerModule(new GuavaModule());
    objectMapper.registerModule(new Jdk8Module());

    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    return objectMapper;
  }
}