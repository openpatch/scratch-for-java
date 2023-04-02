package org.openpatch.scratch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.sound.SoundFile;

public class Applet extends PApplet {
    private final int INITIAL_HEIGHT;
    private boolean debug;
    private static Applet instance;
    private boolean isRunning = false;
    private long numberAssets;
    private long loadedAssets;
    private PImage loading;
    private final int INITIAL_WIDTH;
    private String assets;
    public CopyOnWriteArrayList<StageBox> stages = new CopyOnWriteArrayList<>();
    public int currentStage = -1;

    private class StageBox {
        public Stage stage;
        public String name;

        public StageBox(String name, Stage stage) {
            this.name = name;
            this.stage = stage;
        }
    }

    public Applet(int width, int height, String assets) {
        this.INITIAL_HEIGHT = height;
        this.INITIAL_WIDTH = width;
        this.assets = assets;

        this.registerMethod("pre", this);
        this.registerMethod("mouseEvent", this);
        this.registerMethod("keyEvent", this);
        if (Applet.instance == null) {
            Applet.instance = this;
        }
        this.runSketch();
        thread("loadAssets");
    }

    public static Applet getInstance() {
        return instance;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void addStage(String name, Stage stage) {
        for (StageBox s : this.stages) {
            if (s.name.equals(name)) {
                return;
            }
        }

        this.stages.add(new StageBox(name, stage));

        if (this.currentStage == -1) {
            this.currentStage = 0;
        }
    }

    public Stage getStage(String name) {
        for (StageBox s : this.stages) {
            if (s.name.equals(name)) {
                return s.stage;
            }
        }
        return null;
    }

    public void removeStage(String name) {
        this.stages.removeIf(sb -> sb.name.equals(name));
    }

    public void switchStage(String name) {
        for (int i = 0; i < this.stages.size(); i++) {
            StageBox stageBox = this.stages.get(i);
            if (stageBox.name.equals(name)) {
                this.currentStage = i;
                return;
            }
        }
    }

    /**
     *
     * Use this method to call size().
     *
     * @see PApplet#fullScreen()
     * @see PApplet#setup()
     * @see PApplet#size(int,int)
     * @see PApplet#smooth()
     */
    public void settings() {
        this.size(this.INITIAL_WIDTH, this.INITIAL_HEIGHT, P2D);
    }

    /**
     * Pauses the sketch.
     */
    public void pauseSketch() {
        this.noLoop();
    }

    /**
     * Resumes the sketch.
     */
    public void resumeSketch() {
        this.loop();
    }

    /**
     * Forces a redraw, even if the sketch is paused.
     */
    public void redrawSketch() {
        this.redraw();
    }

    public void runSketch() {
        if (!isRunning) {
            super.runSketch();
            while (this.surface.isStopped()) {
            }
            isRunning = true;
        }
    }

    public void setup() {
        this.windowTitle("Scratch for Java");
        this.imageMode(PConstants.CENTER);
        this.rectMode(PConstants.CENTER);
        loading = this.requestImage("loading.png");
    }

    public void loadAssets() {
        Font.loadFont(Font.defaultFontPath);
        if (this.assets != null) {
            try {
                var imageFiles = Files.find(Paths.get(this.assets),
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile())
                        .map(f -> f.toString())
                        .filter(f -> f.endsWith(".png") || f.endsWith(".jpg") || f.endsWith(".jpeg"))
                        .collect(Collectors.toList());
                var soundFiles = Files.find(Paths.get(this.assets),
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile())
                        .map(f -> f.toString())
                        .filter(f -> f.endsWith(".mp3") || f.endsWith(".wav"))
                        .collect(Collectors.toList());
                var fontFiles = Files.find(Paths.get(this.assets),
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile())
                        .map(f -> f.toString())
                        .filter(f -> f.endsWith(".ttf") || f.endsWith(".otf"))
                        .collect(Collectors.toList());
                numberAssets += imageFiles.size();
                numberAssets += soundFiles.size();
                numberAssets += fontFiles.size();
                for (var file : imageFiles) {
                    Image.loadImage(file);
                    loadedAssets += 1;
                }
                for (var file : fontFiles) {
                    Font.loadFont(file);
                    loadedAssets += 1;
                }
                for (var file : soundFiles) {
                    new SoundFile(this, file, true);
                    loadedAssets += 1;
                }
            } catch (IOException e) {
            }
        }
    }

    private float loadingStatus() {
        return numberAssets > 0 ? loadedAssets / (float) numberAssets : 1;
    }

    public void pre() {
        if (loadingStatus() == 1 && this.stages.size() > 0) {
            try {
                StageBox box = stages.get(this.currentStage);
                box.stage.pre();
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    public void mouseEvent(MouseEvent e) {
        if (loadingStatus() == 1 && this.stages.size() > 0) {
            StageBox box = stages.get(this.currentStage);
            box.stage.mouseEvent(e);
        }
    }

    public void keyEvent(KeyEvent e) {
        if (loadingStatus() == 1 && this.stages.size() > 0) {
            StageBox box = stages.get(this.currentStage);
            box.stage.keyEvent(e);
        }
    }

    public void draw() {
        int sizeStages = this.stages.size();
        if (loadingStatus() < 1) {
            background(0x222222);
            image(loading, width / 2, height / 2);
            // textSize(20);
            // textAlign(CENTER);
            // stroke(0xf58219);
            // text(round(loadingStatus() * 100) + "%", width / 2, height / 2 +
            // loading.height / 2 + 20);
            // textSize(14);
        } else if (sizeStages > 0) {
            if (this.currentStage > sizeStages - 1) {
                this.currentStage = sizeStages;
            } else if (this.currentStage < 0) {
                this.currentStage = 0;
            }
            try {
                StageBox box = stages.get(this.currentStage);
                box.stage.draw();
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
    }
}
