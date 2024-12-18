/**
 * The org.openpatch.scratch package contains classes that provide an API for creating Scratch-like
 * projects in Java.
 *
 * <p>The documentation with many examples can be found at <a
 * href="https://scratch4j.openpatch.org">https://scratch4j.openpatch.org</a>.
 *
 * <p>To create a scratch4j project, you will need to create a class that extends the {@link
 * org.openpatch.scratch.Window} class.
 *
 * <p>The {@link org.openpatch.scratch.Window} class is the main class that represents the window of
 * the project.
 *
 * <pre>
 * import org.openpatch.scratch.Window;
 *
 * public class Game extends Window {
 *      public Game() {
 *          super(800, 600);
 *          this.setStage(new MyStage());
 *      }
 * }
 * </pre>
 *
 * The {@link org.openpatch.scratch.Stage} class represents the stage of the project.
 *
 * <pre>
 * import org.openpatch.scratch.Stage;
 *
 * class MyStage extends Stage {
 *      public MyStage() {
 *          this.add(new MySprite());
 *      }
 * }
 * </pre>
 *
 * The {@link org.openpatch.scratch.Sprite} class represents a sprite in the project.
 *
 * <pre>
 * import org.openpatch.scratch.Sprite;
 *
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
 * </pre>
 *
 * This will create a window with a stage containing a sprite that moves 10 pixels every frame and
 * bounces off the edges of the window.
 */
package org.openpatch.scratch;
