package org.openpatch.scratch.extensions.fs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class File {

  enum FileType {
    XML,
    JSON
  }

  private static ObjectMapper getObjectMapper(FileType type) {
    ObjectMapper objectMapper = new ObjectMapper();
    if (type == FileType.XML) {
      objectMapper = new XmlMapper();
    }
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  public static <T> void save(String path, T obj) {
    save(path, obj, FileType.JSON);
  }

  public static <T> void saveXML(String path, T obj) {
    save(path, obj, FileType.XML);
  }

  private static <T> void save(String path, T obj, FileType type) {
    var mapper = getObjectMapper(type);
    try (Writer writer = new FileWriter(path)) {
      var data = mapper.writeValueAsString(obj);
      writer.write(data);
    } catch (IOException e) {
    }
  }

  public static <T> T load(String path, Class<T> cls) {
    return load(path, cls, FileType.JSON);
  }

  public static <T> T loadXML(String path, Class<T> cls) {
    return load(path, cls, FileType.XML);
  }

  private static <T> T load(String path, Class<T> cls, FileType type) {
    var mapper = getObjectMapper(type);
    try (Reader reader = new FileReader(path)) {
      return mapper.readValue(reader, cls);
    } catch (IOException e) {
      System.out.println(e);
      return null;
    }
  }
}
