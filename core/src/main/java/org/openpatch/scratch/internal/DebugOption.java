package org.openpatch.scratch.internal;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * What debug mode ({@code Window.setDebug(true)}, F12) shows. Each of these can be switched in
 * the debug panel while it runs; {@code -Dscratch4j.debug=hitboxes,labels,…} presets which are on
 * when debug mode starts (names case-insensitive, {@code all} and {@code none} included).
 */
public enum DebugOption {

  /** The hitboxes that count for touching, in front of their layer's sprites. */
  HITBOXES("Hitboxes"),

  /** Every visible sprite's name, position and direction. */
  LABELS("Positions"),

  /** The crosshair at the mouse, the world coordinates under it and the camera's view. */
  MOUSE("Mouse & camera"),

  /** A line between the centres of every pair of sprites that touch. */
  TOUCHING("Touching pairs"),

  /** Outlines of hidden sprites, which take part in nothing. */
  INVISIBLE("Hidden sprites"),

  /** How many clones exist, next to the camera's view. */
  CLONES("Clone count"),

  /** The frame rate the window measures, top right. */
  FPS("Frame rate");

  /** What the debug panel writes next to the checkbox. */
  public final String label;

  DebugOption(String label) {
    this.label = label;
  }

  /**
   * The options {@code -Dscratch4j.debug} names, or the familiar overlay (hitboxes, positions,
   * mouse and camera, frame rate) without the property.
   */
  public static Set<DebugOption> parse(String property) {
    if (property == null) {
      return EnumSet.of(HITBOXES, LABELS, MOUSE, FPS);
    }
    EnumSet<DebugOption> options = EnumSet.noneOf(DebugOption.class);
    if (property.isBlank()) {
      return options;
    }
    for (String name : property.split(",")) {
      name = name.trim();
      if (name.isEmpty()) {
        continue;
      }
      if (name.equalsIgnoreCase("all")) {
        options.addAll(EnumSet.allOf(DebugOption.class));
        continue;
      }
      try {
        options.add(valueOf(name.toUpperCase(Locale.ROOT)));
      } catch (IllegalArgumentException e) {
        System.err.println("Scratch for Java: unknown debug option '" + name + "'");
      }
    }
    return options;
  }
}
