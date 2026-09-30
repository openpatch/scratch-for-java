---
name: Shakespeare
---

# Shakespeare

An example showing the use of the Text class. 

![shakespeare example](/assets/shakespeare.gif)

## Run it here

A population of 5000 random phrases evolves, generation by generation, until one
of them is the line it is looking for. The best phrase so far is written in a
font loaded from a file.

:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="assets/Singkong.ttf" src="/examples/shakespeare/assets/Singkong.ttf"

```java Shakespeare.java

void main() {
  Text.useFontSizes(14, 20);
  new Shakespeare();
}

class Shakespeare extends Stage {

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
    this.bestPhrase.addFont("comic", "assets/Singkong.ttf");
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
}

class Population {

  private int generations;
  private boolean finished;
  private float mutationrate;
  private String target;
  private DNA[] population;
  private DNA[] matingpool;
  private String best;

  public Population(String pTarget, float pMutationrate, int pNumber) {
    this.population = new DNA[pNumber];
    this.mutationrate = pMutationrate;
    this.target = pTarget;
    this.best = "";
    this.generations = 0;
    this.finished = false;

    for (int i = 0; i < pNumber; i++) {
      this.population[i] = new DNA(pTarget.length());
    }
  }

  public void calculateFit() {
    for (int i = 0; i < this.population.length; i++) {
      this.population[i].calculateFit(this.target);
    }
  }

  public void naturalselection() {
    int requiredPlaces = 0;
    for (int i = 0; i < this.population.length; i++) {
      requiredPlaces += (int) (this.population[i].getFit() * 100);
    }

    this.matingpool = new DNA[requiredPlaces];
    int nextPlace = 0;
    for (int i = 0; i < this.population.length; i++) {
      int place = (int) (this.population[i].getFit() * 100);
      for (int j = 0; j < place; j++) {
        this.matingpool[nextPlace] = this.population[i];
        nextPlace++;
      }
    }
  }

  public void newGeneration() {
    if (matingpool.length > 1) {

      for (int i = 0; i < this.population.length; i++) {
        int a = (int) (Math.random() * matingpool.length);
        int b = (int) (Math.random() * matingpool.length);
        DNA mateA = matingpool[a];
        DNA mateB = matingpool[b];
        DNA child = mateA.crossover(mateB);
        child.mutate(mutationrate);
        population[i] = child;
      }
    } else {
      for (int i = 0; i < this.population.length; i++) {
        population[i].mutate(mutationrate);
      }
    }

    this.generations += 1;
  }

  public String getBest() {
    return this.best;
  }

  public void evaluate() {
    float worldrecord = 0;

    for (int i = 0; i < this.population.length; i++) {
      if (this.population[i].getFit() > worldrecord) {
        best = this.population[i].getPhrase();
        worldrecord = this.population[i].getFit();
      }
    }

    if (worldrecord == 1) {
      this.finished = true;
    }
  }

  public boolean isFinished() {
    return this.finished;
  }

  public int getGeneration() {
    return this.generations;
  }

  public float gibDurchschnittlichenFit() {
    float sum = 0;
    for (int i = 0; i < this.population.length; i++) {
      sum += this.population[i].getFit();
    }

    return sum / this.population.length;
  }

  public String getAllPhrases(int limit) {
    String all = "";
    int displayLimit = Math.min(this.population.length, limit);

    for (int i = 0; i < displayLimit; i++) {
      all += this.population[i].getPhrase() + "\n";
    }

    return all;
  }
}

class DNA {
  private char[] genes;
  private float fit;

  public DNA(int numberOfGenes) {
    genes = new char[numberOfGenes];
    for (int i = 0; i < genes.length; i++) {
      genes[i] = this.randomChar();
    }
  }

  public void calculateFit(String target) {
    int score = 0;
    for (int i = 0; i < genes.length; i++) {
      if (genes[i] == target.charAt(i)) {
        score++;
      }
    }
    fit = (float) score / (float) target.length();
  }

  public float getFit() {
    return fit;
  }

  public String getPhrase() {
    return new String(genes);
  }

  public DNA crossover(DNA mate) {
    DNA kind = new DNA(genes.length);

    int cutoff = (int) (Math.random() * genes.length);

    for (int i = 0; i < genes.length; i++) {
      if (i > cutoff) {
        kind.genes[i] = this.genes[i];
      } else {
        kind.genes[i] = mate.genes[i];
      }
    }

    return kind;
  }

  public void mutate(float mutationrate) {
    for (int i = 0; i < genes.length; i++) {
      if (Math.random() < mutationrate) {
        genes[i] = this.randomChar();
      }
    }
  }

  private char randomChar() {
    // siehe https://www.ascii-code.com/
    char zeichen = (char) (Math.random() * (127 - 32) + 32);
    return zeichen;
  }
}
```

:::

To run it on your own computer, copy the folder `assets` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/shakespeare) next to the program.

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/shakespeare
