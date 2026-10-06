---
name: Smart Rocket
---

# Smart Rocket

An example which demonstrates the usage of the math classes Vector2 and Random.

![smart rocket example](/assets/smart-rocket.gif)

## Run it here

Every rocket flies by its own random genes. After each generation the rockets
that came closest to the target have the most children, and the swarm learns to
find it. Hold the mouse down to move the target.

<!-- demo: smartRocket -->
:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="assets/rocket.png" src="/examples/smart-rocket/assets/rocket.png"
@file dest="assets/target.png" src="/examples/smart-rocket/assets/target.png"

```java SmartRocket.java

void main() {
  // scratch4j:begin options (managed by the project settings)
  // scratch4j:end options
  new SmartRocket();
}

class SmartRocket extends Window {
  public SmartRocket() {
    super(800, 600, "assets");

    // scratch4j:begin window (managed by the project settings)
    this.setStage(new Level());
    // scratch4j:end window
  }
}

class Level extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  // scratch4j:end fields

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
    // the four lines are centred on this point, so it is far enough below the
    // top edge for the first one to fit
    this.statistiken.setPosition(-this.getWidth() / 2 + 10, this.getHeight() / 2 - 40);
    this.statistiken.setAlign(TextAlign.LEFT);
    this.add(this.statistiken);

    // scratch4j:begin setup (managed by the stage designer)
    // scratch4j:end setup
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

class Ziel extends Sprite {

  public Ziel() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("target", "assets/target.png");
    this.setHitbox(10, 38, 10, 10, 38, 10, 38, 38);
    // scratch4j:end setup
  }
}

class Population {
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

class Rocket extends Sprite {
  private Vector2 geschwindigkeit;
  private Vector2 beschleunigung;

  private double fit;
  private DNA dna;
  private int geneZaehler = 0;
  private Ziel ziel;

  public Rocket(Vector2 pPosition, DNA pDna, Ziel pZiel) {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("rocket", "assets/rocket.png");
    this.setHitbox(68, 27, 68, 20, 76, 21, 76, 26);
    // scratch4j:end setup
    this.setPosition(pPosition);
    this.beschleunigung = new Vector2();
    this.geschwindigkeit = new Vector2();
    this.dna = pDna;
    this.ziel = pZiel;
  }

  public void berechneFit() {
    double d = this.distanceToSprite(ziel);
    fit = 1 / d * 1 / d;
  }

  public double gibFit() {
    return fit;
  }

  public DNA gibDNA() {
    return dna;
  }

  public void run() {
    if (!this.isTouchingSprite(ziel)) {
      beschleunigung = beschleunigung.add(dna.gibGene()[geneZaehler]);
      geneZaehler = (geneZaehler + 1) % dna.gibGene().length;
      geschwindigkeit = geschwindigkeit.add(beschleunigung);
      move(geschwindigkeit);
      beschleunigung = beschleunigung.multiply(0);
    } else {
      this.setTint(200);
    }
  }
}

class DNA {
  private Vector2[] gene;
  private double maximaleKraft = 0.5;

  public DNA(int pLebenszeit) {
    gene = new Vector2[pLebenszeit];
    for (int i = 0; i < gene.length; i++) {
      double winkel = Random.random(360);
      double kraft = Random.random(maximaleKraft);
      gene[i] = Vector2.fromPolar(kraft, winkel);
    }
  }

  public DNA(Vector2[] pGene) {
    this.gene = pGene;
  }

  public DNA crossover(DNA partner) {
    Vector2[] geneKind = new Vector2[gene.length];

    var cutoff = Random.random(gene.length);

    for (int i = 0; i < gene.length; i++) {
      if (i > cutoff) {
        geneKind[i] = gene[i];
      } else {
        geneKind[i] = partner.gene[i];
      }
    }

    return new DNA(geneKind);
  }

  public void mutiere(double mutationsrate) {
    for (int i = 0; i < gene.length; i++) {
      if (Random.random() < mutationsrate) {
        double winkel = Random.random(360);
        double kraft = Random.random(maximaleKraft);
        gene[i] = Vector2.fromPolar(kraft, winkel);
      }
    }
  }

  public Vector2[] gibGene() {
    return gene;
  }
}
```

:::

To run it on your own computer, copy the folder `assets` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/smartRocket) next to the program.

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/smartRocket
