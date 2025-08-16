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

import org.openpatch.scratch.internal.Applet;

/**
 * The File class provides methods to save and load objects to and from files in
 * JSON or XML format.
 * It supports saving and loading objects with automatic detection of file type
 * based on the method
 * used. The class uses Jackson ObjectMapper for JSON and XmlMapper for XML
 * serialization and
 * deserialization.
 *
 * <p>
 * Usage examples:
 *
 * <pre>{@code
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
 * }</pre>
 *
 * <p>
 * Note: The file paths can be absolute or relative. If a relative path is
 * provided, the class
 * will attempt to load the file from the classpath.
 *
 * <p>
 * Supported file types:
 *
 * <ul>
 * <li>JSON
 * <li>XML
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
   * Saves the given object to a file at the specified path in the specified file
   * type format.
   *
   * @param <T>  the type of the object to be saved
   * @param path the path where the file will be saved
   * @param obj  the object to be saved
   * @param type the file type format in which the object will be saved
   */
  private static <T> void save(String path, T obj, FileType type) {
    var mapper = getObjectMapper(type);
    path = Applet.getPath(path);
    java.io.File file = new java.io.File(path);

    // Ensure parent directories exist
    java.io.File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }

    try (Writer writer = new FileWriter(path)) {
      var data = mapper.writeValueAsString(obj);
      writer.write(data);
    } catch (IOException e) {
      e.printStackTrace();
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
    return load(path, cls, FileType.JSON, false);
  }

  /**
   * Loads an object of the specified class from a file at the given path, or
   * creates a new one if the file does not exist.
   *
   * @param <T>  the type of the object to be loaded
   * @param path the path to the file from which the object is to be loaded
   * @param cls  the class of the object to be loaded
   * @return the loaded object of the specified class, or a new instance if the
   *         file does not exist
   */
  public static <T> T loadOrCreate(String path, Class<T> cls) {
    return load(path, cls, FileType.JSON, true);
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
    return load(path, cls, FileType.XML, false);
  }

  /**
   * Loads an object of the specified class from an XML file at the given path,
   * or creates a new one if the file does not exist.
   *
   * @param <T>  the type of the object to be loaded
   * @param path the path to the XML file
   * @param cls  the class of the object to be loaded
   * @return the object loaded from the XML file, or a new instance if the file
   *         does not exist
   */
  public static <T> T loadXMLOrCreate(String path, Class<T> cls) {
    return load(path, cls, FileType.XML, true);
  }

  private static <T> T load(String path, Class<T> cls, FileType type, boolean createIfMissing) {
    var mapper = getObjectMapper(type);
    path = Applet.getPath(path);
    java.io.File file = new java.io.File(path);

    // Create file if missing and flag is set
    if (!file.exists() && createIfMissing) {
      try {
        file.getParentFile().mkdirs(); // ensure parent directories exist
        file.createNewFile(); // creates empty file
        // Optionally: write default content so deserialization won't fail
        try (Writer writer = new FileWriter(file)) {
          writer.write(mapper.writeValueAsString(cls.getDeclaredConstructor().newInstance()));
        }
      } catch (Exception e) {
        System.err.println("Could not create file: " + e);
        return null;
      }
    }

    try (Reader reader = new FileReader(file)) {
      return mapper.readValue(reader, cls);
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
  }
}
