package org.openpatch.scratch;

/**
 * Thrown when a program cannot go on, for example because a costume or a sound
 * could not be loaded.
 *
 * <p>
 * A full explanation of what went wrong, and usually a suggestion for how to fix
 * it, is printed before this is thrown, so the message here is deliberately
 * short.
 *
 * <p>
 * You are not expected to catch it. It exists so that a failure stops your
 * program rather than the whole development environment, which is what happened
 * when the library used to end the process itself.
 *
 * @ignore-in-docs
 */
public class ScratchException extends RuntimeException {

  /**
   * Creates the exception.
   *
   * @param message a short summary; the details are already on the console
   */
  public ScratchException(String message) {
    super(message);
  }
}
