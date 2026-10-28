package reference.FileSave;

import org.openpatch.scratch.save.*;

public class FileSave {

  public FileSave() {
    var settings = new Settings();
    settings.name = "Hi";
    settings.value = 10;
    File.save("settings.json", settings);

    Settings loadedSettings = File.load("settings.json", Settings.class);
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
