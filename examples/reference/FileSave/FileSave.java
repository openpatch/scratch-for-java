package FileSave;

import org.openpatch.scratch.extensions.fs.*;

public class FileSave {

  public FileSave() {
    var settings = new Settings();
    settings.name = "Hi";
    settings.value = 10;
    File.saveXML("settings.xml", settings);
    File.save("settings.json", settings);

    Settings loadedSettings = File.loadXML("settings.xml", Settings.class);
    System.out.println(loadedSettings.name);

    loadedSettings = File.load("settings.json", Settings.class);
    System.out.println(loadedSettings.name);
  }

  public static void main(String[] args) {
    new FileSave();
  }
}

class Settings {

  public String name;
  public int value;
}
