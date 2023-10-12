package SmartRocket;

import org.openpatch.scratch.extensions.math.*;

public class DNA {
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
