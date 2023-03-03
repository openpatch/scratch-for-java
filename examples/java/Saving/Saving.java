import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.File;

public class Saving {

    public Saving() {
        var settings = new Settings("Hi", 10);
        File.save("settings", settings);

        var loadedSettings = File.load("settings", Settings.class);
        System.out.println(loadedSettings.name);
    }

    public static void main(String[] args) {
        new Saving();
    }

}
