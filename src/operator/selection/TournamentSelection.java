package operator.selection; // class ini berada di package operator.selection

import ga.Individual; // pakai class Individual dari package ga
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentSelection implements SelectionOperator {
    private long k;

    public TournamentSelection(long k) {
        this.k = k; //number of individuals chosen for the pool from population
    }

    @Override
    public Individual select(List<Individual> population, Random random) {
        List<Individual> pool = new ArrayList<>(); //Pool for tournament
        
        //adds k amt of individuals to the tournament pool at random indexes 
        for(int i = 0; i < this.k; i++){
            int index = random.nextInt(population.size());
            Individual chosen = population.get(index);
            pool.add(chosen);
        }

        //sort
        pool.sort(null); // null = pakai urutan compareTo milik Individual (fitness terbesar dulu)
        //choose the highest
        return pool.get(0);
    }
}
