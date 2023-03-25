public class FileSave {

    class Settings {
        private String name;

        public Settings(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

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
