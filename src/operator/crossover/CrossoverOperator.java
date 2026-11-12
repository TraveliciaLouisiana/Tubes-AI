package operator.crossover; 

import chromosome.Chromosome; 
import ga.Individual;
import java.util.Random;

public interface CrossoverOperator {
    public Individual crossover(Chromosome a, Chromosome b, Random random);
}
