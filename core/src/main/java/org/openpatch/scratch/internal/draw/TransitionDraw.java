package org.openpatch.scratch.internal.draw;

/**
 * A transition in progress: the old stage's last frame and how far along it is. The draw list
 * it belongs to is the new stage's.
 *
 * @param from the old stage's draw list, as it was when the transition started
 * @param name a built-in transition ("fade", "crossfade", "slide-left", …) or a shader's file
 * @param source a transition shader's body; null for the built-in ones
 * @param progress from 0 to 1
 */
public record TransitionDraw(DrawList from, String name, String source, double progress) {}
