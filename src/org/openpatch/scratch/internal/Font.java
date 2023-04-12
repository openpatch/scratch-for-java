package org.openpatch.scratch.internal;

import java.util.concurrent.ConcurrentHashMap;

import processing.core.PFont;

public class Font {
    private String name;
    private ConcurrentHashMap<Integer, PFont> fontMap;

    public static String defaultFontName = "default";
    public static String defaultFontPath = "UbuntuMono-Regular.ttf";
    private static int[] sizes = { 8, 12, 14, 16, 20, 32, 48, 64, 128 };
    private static final ConcurrentHashMap<String, ConcurrentHashMap<Integer, PFont>> fonts = new ConcurrentHashMap<>();

    public Font(String name, String path) {
        this.name = name;
        this.fontMap = new ConcurrentHashMap<>();

        this.fontMap = loadFont(path);
    }

    public Font(Font font) {
        this.name = font.name;
        this.fontMap = new ConcurrentHashMap<>(font.fontMap);
    }

    public String getName() {
        return this.name;
    }

    public static ConcurrentHashMap<Integer, PFont> loadFont(String path) {
        var fontMap = fonts.getOrDefault(path, new ConcurrentHashMap<>());
        for (int size : sizes) {
            if (fontMap.containsKey(size)) {
                return fontMap;
            } else {
                PFont font = Applet.getInstance().createFont(path, size, true);
                fontMap.put(size, font);
            }
        }
        fonts.put(path, fontMap);
        return fontMap;
    }

    public PFont getFont(int targetSize) {
        int actualSize = sizes[2];
        int bestSizeDifference = Math.abs(actualSize - targetSize);

        for (int size : sizes) {
            int sizeDifference = Math.abs(size - targetSize);
            if (bestSizeDifference > sizeDifference) {
                bestSizeDifference = sizeDifference;
                actualSize = size;
            }
        }

        return fontMap.get(actualSize);
    }
}
