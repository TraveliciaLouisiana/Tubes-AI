package operator.selection;

import ga.Individual;
import java.util.List;
import java.util.Random;

public interface SelectionOperator {
    public Individual select(List<Individual> population, Random random);
}
