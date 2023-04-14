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
    this.ziel.setPosition(600, 20);
    this.add(ziel);
    this.population =
        new Population(
            this,
            this.mutationsrate,
            this.populationsgroesse,
            new Vector2(Window.getInstance().getWidth() / 2, Window.getInstance().getHeight() - 20),
            this.ziel,
            this.lebenszeit);

    this.statistiken = new Text();
    this.statistiken.setPosition(10, 10);
    this.statistiken.setAlign(TextAlign.LEFT);
    this.add(statistiken);
  }

  public void run() {
    if (ziel != null && this.isMouseDown()) {
      ziel.setPosition(this.getMouseX(), this.getMouseY());
    }
    if (population != null) {
      if (zeit >= lebenszeit) {
        zeit = 0;
        this.population.berechneFit();
        this.population.natuerlicheSelektion();
        this.population.neueGeneration();
      } else {
        zeit++;
      }
      String statistikText = "";
      statistikText += "Generationen: " + population.gibGenerationen() + "\n";
      statistikText += "Lebenszeit: " + (lebenszeit - zeit) + "\n";
      statistikText += "Populationsgröße: " + populationsgroesse + "\n";
      statistikText += "Mutationsrate: " + Math.round(mutationsrate * 100) + "%\n";

      this.statistiken.showText(statistikText);
    }
  }
}
