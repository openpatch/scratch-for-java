package Saving;

import org.openpatch.scratch.extensions.fs.*;

public class Saving {

  public Saving() {
    var settings = new ExampleSettings();
    settings.name = "Hi";
    settings.groesse = 10;
    File.save("Saving/settings.json", settings);

    var loadedSettings = File.load("Saving/settings.json", ExampleSettings.class);
    System.out.println(loadedSettings.name);
  }

  public static void main(String[] args) {
    new Saving();
  }
}
