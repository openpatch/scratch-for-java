/**
 * The org.openpatch.scratch package contains classes that provide an API for creating Scratch-like
 * projects in Java.
 *
 * The documentation with many examples can be found at <a
 * href="https://scratch4j.openpatch.org">https://scratch4j.openpatch.org</a>.
 *
 * To create a scratch4j project, you will need to create a class that extends the {@link
 * org.openpatch.scratch.Window} class.
 *
 * The {@link org.openpatch.scratch.Window} class is the main class that represents the window of
 * the project.
 *
 * <pre>
 * {@code
 *  *
 * public class Game extends Window {
 *      public Game() {
 *          super(800, 600);
 *          this.setStage(new MyStage());
 *      }
 * }
 * }
 * </pre>
 *
 * The {@link org.openpatch.scratch.Stage} class represents the stage of the project.
 *
 * <pre>
 * {@code
 *  *
 * class MyStage extends Stage {
 *      public MyStage() {
 *          this.add(new MySprite());
 *      }
 * }
 * }
 * </pre>
 *
 * The {@link org.openpatch.scratch.Sprite} class represents a sprite in the project.
 *
 * <pre>
 * {@code
 *  *
 * class MySprite extends Sprite {
 *      public MySprite() {
 *          this.setCostume("cat_sitting", "cat.png");
 *      }
 *
 *      public void run() {
 *          this.ifOnEdgeBounce();
 *          this.move(10);
 *      }
 * }
 * }
 * </pre>
 *
 * This will create a window with a stage containing a sprite that moves 10 pixels every frame and
 * bounces off the edges of the window.
 *
 * <h2>Running the examples here</h2>
 *
 * Nearly every method on the following pages comes with an example you can start
 * and change without leaving the page. Those run in your browser rather than on
 * your own computer, and there is one difference worth knowing about: a whole
 * number that is a {@code double} prints here without its decimal part, so
 * {@code println("x: " + mySprite.getX())} says {@code x: 0} in the browser and
 * {@code x: 0.0} when you run the same program yourself. The value is the same;
 * only the way it is written out differs.
 *
 * @name-in-docs Documentation
 */
package org.openpatch.scratch;
