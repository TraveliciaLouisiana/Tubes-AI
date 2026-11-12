package operator.mutation;

import ga.Individual; 
import java.util.Random;

public interface MutationOperator {
    public void mutation(Individual a, double pm, Random random);
}
