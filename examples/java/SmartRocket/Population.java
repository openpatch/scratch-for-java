import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.math.*;

public class Population {
  private double mutationsrate;
  private Rocket[] population;
  private Rocket[] partnerpool;
  private int generationen;
  private Vector2 startPosition;
  private Ziel ziel;
  private int lebenszeit;
  private Level stage;

  public Population(
      Level pStage,
      double pMutationsrate,
      int pPopulationsgroesse,
      Vector2 pStartPosition,
      Ziel pZiel,
      int pLebenszeit) {
    mutationsrate = pMutationsrate;
    stage = pStage;
    population = new Rocket[pPopulationsgroesse];
    generationen = 0;
    startPosition = pStartPosition;
    lebenszeit = pLebenszeit;
    ziel = pZiel;

    for (int i = 0; i < population.length; i++) {
      population[i] = new Rocket(startPosition, new DNA(lebenszeit), ziel);
      stage.add(population[i]);
    }
  }

  public void berechneFit() {
    for (int i = 0; i < this.population.length; i++) {
      this.population[i].berechneFit();
    }
  }

  public void natuerlicheSelektion() {
    int noetigePlaetze = 0;
    double maxFit = gibMaxFit();
    for (int i = 0; i < this.population.length; i++) {
      int plaetze = (int) Operators.map(population[i].gibFit(), 0, maxFit, 0, 100);
      noetigePlaetze += plaetze;
    }
    this.partnerpool = new Rocket[noetigePlaetze];
    int naechsterPlatz = 0;
    for (int i = 0; i < this.population.length; i++) {
      int plaetze = (int) Operators.map(population[i].gibFit(), 0, maxFit, 0, 100);
      for (int j = 0; j < plaetze; j++) {
        this.partnerpool[naechsterPlatz] = this.population[i];
        naechsterPlatz++;
      }
    }
  }

  public void neueGeneration() {
    stage.remove(Rocket.class);

    for (int i = 0; i < this.population.length; i++) {
      int a = (int) (Math.random() * partnerpool.length);
      int b = (int) (Math.random() * partnerpool.length);
      Rocket partnerA = partnerpool[a];
      Rocket partnerB = partnerpool[b];
      DNA dna = partnerA.gibDNA().crossover(partnerB.gibDNA());
      dna.mutiere(mutationsrate);

      population[i] = new Rocket(startPosition, dna, ziel);
      stage.add(population[i]);
    }

    this.generationen += 1;
  }

  public int gibGenerationen() {
    return generationen;
  }

  public double gibMaxFit() {
    double max = 0;
    for (int i = 0; i < this.population.length; i++) {
      if (max < population[i].gibFit()) {
        max = population[i].gibFit();
      }
    }

    return max;
  }

  public double gibDurchschnittlichenFit() {
    double summe = 0;
    for (int i = 0; i < this.population.length; i++) {
      summe += this.population[i].gibFit();
    }

    return summe / this.population.length;
  }
}
