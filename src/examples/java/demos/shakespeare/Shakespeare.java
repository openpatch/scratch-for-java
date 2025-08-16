package demos.shakespeare;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.text.*;

public class Shakespeare extends Stage {

  private Text bestPhrase;
  private Text allPhrases;
  private Text statistics;

  private String target;
  private int populationsize;
  private float mutationrate;

  private Population population;

  public Shakespeare() {
    super(800, 600);

    this.target = "To be, or not to be, that is the question.";
    this.populationsize = 5000;
    this.mutationrate = 0.01f;

    this.population = new Population(this.target, this.mutationrate, this.populationsize);

    this.bestPhrase = new Text();
    this.bestPhrase.addFont("comic", "demos/shakespeare/assets/Singkong.ttf");
    this.bestPhrase.setPosition(-390, 250);
    this.bestPhrase.setAlign(TextAlign.LEFT);
    this.bestPhrase.setTextSize(20);
    this.bestPhrase.switchFont("comic");
    this.bestPhrase.setTextColor(200, 50, 50);
    this.add(this.bestPhrase);

    this.allPhrases = new Text();
    this.allPhrases.setPosition(0, 250);
    this.allPhrases.setAlign(TextAlign.LEFT);
    this.add(this.allPhrases);

    this.statistics = new Text();
    this.statistics.setPosition(-390, 100);
    this.statistics.setAlign(TextAlign.LEFT);
    this.add(this.statistics);
  }

  public void run() {
    // run may execute before the execution of the constructor of Shakespear
    // is finished. Therefore, we need to test if population is set.
    // This does only relevant for single Stage mode.
    if (this.population != null && !this.population.isFinished()) {
      this.population.naturalselection();
      this.population.newGeneration();
      this.population.calculateFit();
      this.population.evaluate();

      String statisticText = "";
      statisticText += "Generations: " + this.population.getGeneration() + "\n";
      statisticText += "Average Fit: " + this.population.gibDurchschnittlichenFit() + "\n";
      statisticText += "Populationsize: " + this.populationsize + "\n";
      statisticText += "Mutationrate: " + Math.round(this.mutationrate * 100) + "%\n";

      this.statistics.showText(statisticText);
      this.bestPhrase.showText("Best Phrase:\n" + this.population.getBest());
      this.allPhrases.showText("All Phrases:\n" + this.population.getAllPhrases(25));
    }
  }

  public static void main(String[] args) {
    Text.FONT_SIZES = new int[] { 14, 20 };
    new Shakespeare();
  }
}
