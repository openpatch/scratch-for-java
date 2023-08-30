import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.math.*;
import org.openpatch.scratch.extensions.text.*;

public class Level extends Stage {
  private Text statistiken;

  private Ziel ziel;
  private Population population;
  private double mutationsrate = 0.01;
  private int populationsgroesse = 20;
  // Wie viele Frames lebt eine Generation?
  private int lebenszeit = 180;
  // Wie viele Frames sind vergangen?
  private int zeit = 0;

  public Level() {
    this.ziel = new Ziel();
    this.ziel.setPosition(100, 20);
    this.add(this.ziel);
    this.population = new Population(
        this,
        this.mutationsrate,
        this.populationsgroesse,
        new Vector2(0, -this.getHeight() + 20),
        this.ziel,
        this.lebenszeit);

    this.statistiken = new Text();
    this.statistiken.setPosition(-this.getWidth() / 2 + 10, this.getHeight() / 2 - 10);
    this.statistiken.setAlign(TextAlign.LEFT);
    this.add(this.statistiken);
  }

  public void run() {
    if (this.ziel != null && this.isMouseDown()) {
      this.ziel.setPosition(this.getMouseX(), this.getMouseY());
    }
    if (this.population != null) {
      if (this.zeit >= this.lebenszeit) {
        this.zeit = 0;
        this.population.berechneFit();
        this.population.natuerlicheSelektion();
        this.population.neueGeneration();
      } else {
        this.zeit++;
      }
      String statistikText = "";
      statistikText += "Generationen: " + this.population.gibGenerationen() + "\n";
      statistikText += "Lebenszeit: " + (this.lebenszeit - this.zeit) + "\n";
      statistikText += "Populationsgröße: " + this.populationsgroesse + "\n";
      statistikText += "Mutationsrate: " + Math.round(this.mutationsrate * 100) + "%\n";

      this.statistiken.showText(statistikText);
    }
  }
}
