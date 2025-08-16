package demos.smartRocket;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.math.*;

public class Rocket extends Sprite {
  private Vector2 geschwindigkeit;
  private Vector2 beschleunigung;

  private double fit;
  private DNA dna;
  private int geneZaehler = 0;
  private Ziel ziel;

  public Rocket(Vector2 pPosition, DNA pDna, Ziel pZiel) {
    this.addCostume("rocket", "demos/smartRocket/assets/rocket.png");
    this.setPosition(pPosition);
    this.setHitbox(68, 27, 68, 20, 76, 21, 76, 26);
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
