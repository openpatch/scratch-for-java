import org.openpatch.scratch.extensions.fs.*;

public class FileSave {

  public FileSave() {
    var settings = new Settings("Hi", 10);
    File.save("settings", settings);

    Settings loadedSettings = File.load("settings", Settings.class);
    System.out.println(loadedSettings.getName());
  }

  public static void main(String[] args) {
    new FileSave();
  }
}

class Settings {

  private String name;
  private int value;

  public Settings(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }
}
