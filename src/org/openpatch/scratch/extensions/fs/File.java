package org.openpatch.scratch.extensions.fs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;

public class File {

  public static <T> void save(String path, T obj) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(obj);
    if (!path.endsWith(".json")) {
      path = path + ".json";
    }
    try (Writer writer = new FileWriter(path)) {
      writer.write(json);
    } catch (IOException e) {
    }
  }

  public static <T> T load(String path, Class<T> cls) {
    Gson gson =
        new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
            .create();
    if (!path.endsWith(".json")) {
      path = path + ".json";
    }
    try (Reader reader = new FileReader(path)) {
      return gson.fromJson(reader, cls);
    } catch (IOException e) {
      return null;
    }
  }
}
