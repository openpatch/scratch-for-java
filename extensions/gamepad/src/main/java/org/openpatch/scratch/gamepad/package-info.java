/**
 * Controller support: up to four gamepads, polled every tick. Ask them anywhere with
 * {@link Gamepad}, or implement {@link GamepadListener} on the stage or a sprite to be told about
 * button presses and controllers connecting. Buttons are in Xbox positions ({@code A} is Cross
 * on a PlayStation controller, see {@link GamepadButton}); sticks report y up, as on the stage,
 * and a stick near its middle reports (0, 0). Controllers without a known layout are skipped.
 * There is no rumble.
 *
 * <p>In the {@code -all} jar and Studio; with Maven add the artifact {@code scratch-gamepad}.
 *
 * @name-in-docs Gamepad
 */
package org.openpatch.scratch.gamepad;
