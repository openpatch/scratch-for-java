/**
 * The draw list: what the game thread hands the renderer at the end of a batch of ticks.
 * Immutable records only; the renderer reads nothing else of the scene,
 * so it needs no locks and learner objects can change while a frame is drawn.
 */
package org.openpatch.scratch.internal.draw;
