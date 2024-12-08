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
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * The File class provides methods to save and load objects to and from files in JSON or XML format.
 * It supports saving and loading objects with automatic detection of file type based on the method used.
 * The class uses Jackson ObjectMapper for JSON and XmlMapper for XML serialization and deserialization.
 * 
 * <p>Usage examples:</p>
 * <pre>
 * {@code
 * // Save an object as JSON
 * File.save("/path/to/file.json", myObject);
 * 
 * // Save an object as XML
 * File.saveXML("/path/to/file.xml", myObject);
 * 
 * // Load an object from JSON
 * MyClass obj = File.load("/path/to/file.json", MyClass.class);
 * 
 * // Load an object from XML
 * MyClass obj = File.loadXML("/path/to/file.xml", MyClass.class);
 * }
 * </pre>
 * 
 * <p>Note: The file paths can be absolute or relative. If a relative path is provided, the class will attempt to load the file from the classpath.</p>
 * 
 * <p>Supported file types:</p>
 * <ul>
 *   <li>JSON</li>
 *   <li>XML</li>
 * </ul>
 */
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

  /**
   * Saves the given object to the specified path in the default file type (JSON).
   *
   * @param <T>  the type of the object to be saved
   * @param path the path where the object should be saved
   * @param obj  the object to be saved
   */
  public static <T> void save(String path, T obj) {
    save(path, obj, FileType.JSON);
  }

  /**
   * Saves the given object as an XML file at the specified path.
   *
   * @param <T>  the type of the object to be saved
   * @param path the path where the XML file will be saved
   * @param obj  the object to be saved as XML
   */
  public static <T> void saveXML(String path, T obj) {
    save(path, obj, FileType.XML);
  }

  /**
   * Saves the given object to a file at the specified path in the specified file type format.
   *
   * @param <T>   the type of the object to be saved
   * @param path  the path where the file will be saved
   * @param obj   the object to be saved
   * @param type  the file type format in which the object will be saved
   */
  private static <T> void save(String path, T obj, FileType type) {
    var mapper = getObjectMapper(type);
    path = getPath(path);
    try (Writer writer = new FileWriter(path)) {
      var data = mapper.writeValueAsString(obj);
      writer.write(data);
    } catch (IOException e) {
    }
  }

  /**
   * Loads an object of the specified class from a file at the given path.
   *
   * @param <T>  the type of the object to be loaded
   * @param path the path to the file from which the object is to be loaded
   * @param cls  the class of the object to be loaded
   * @return the loaded object of the specified class
   */
  public static <T> T load(String path, Class<T> cls) {
    return load(path, cls, FileType.JSON);
  }

  /**
   * Loads an object of the specified class from an XML file at the given path.
   *
   * @param <T>  the type of the object to be loaded
   * @param path the path to the XML file
   * @param cls  the class of the object to be loaded
   * @return the object loaded from the XML file
   */
  public static <T> T loadXML(String path, Class<T> cls) {
    return load(path, cls, FileType.XML);
  }

  private static String getPath(String path) {

    // add support for ~
    path = path.replaceFirst("^~", System.getProperty("user.home"));
    // if an absulte path is provied use this, otherwise try loading from classpath
    try {
      if (Path.of(path).isAbsolute()) {
        return path;
      }
      var systemPath = ClassLoader.getSystemResource("").toURI().resolve(path);
      if (systemPath != null) {
        return Path.of(systemPath).toString();
      }
      return path;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static <T> T load(String path, Class<T> cls, FileType type) {
    var mapper = getObjectMapper(type);
    path = getPath(path);
    try (Reader reader = new FileReader(path)) {
      return mapper.readValue(reader, cls);
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }
}
