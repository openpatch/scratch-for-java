public class DNA {
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