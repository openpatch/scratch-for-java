public class Population {

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
