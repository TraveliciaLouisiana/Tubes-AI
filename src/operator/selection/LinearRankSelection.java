package operator.selection; 

import ga.Individual; 
import java.util.List;
import java.util.Random;

//linear rank selection using bakers formula
public class LinearRankSelection implements SelectionOperator {

    private double selectivePressure;

    public LinearRankSelection(double selectivePressure) {
        this.selectivePressure = selectivePressure;
    }

    @Override
    public Individual select(List<Individual> population, Random random) {
        int populationNum = population.size();

        double stoppingIndex = random.nextDouble(0,1);
        double sumProbability = 0;
        
        List<Individual> sortedPopulation = population;
        sortedPopulation.sort(null); // null = pakai urutan compareTo milik Individual (fitness terbesar dulu)

        //bakers probability function
        for(int index = 0; index < populationNum; index++){
            double probability = (1.0/populationNum)* //0 to 1
                        (selectivePressure-(2*selectivePressure-2)*
                        ((index-1)/(populationNum-1)));
            sumProbability+=probability;
            if(sumProbability >= stoppingIndex){
                return sortedPopulation.get(index);
            }
        }
        return sortedPopulation.get(populationNum-1);  
    }
}
