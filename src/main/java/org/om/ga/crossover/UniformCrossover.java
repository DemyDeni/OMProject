package org.om.ga.crossover;

import org.om.ga.Task;
import org.om.ga.mutation.UniformMutation;

import java.util.ArrayList;
import java.util.Random;

public class UniformCrossover implements Crossover {
    private final double percentageOfGenesToReplace = 0.04;
    @Override
    public CrossoverAndMutationData cross(CrossoverData crossoverData) {
        Random random = new Random();
        UniformMutation mutation = new UniformMutation();
        int numberOfGenesToReplace = (int) (crossoverData.list1.size() * percentageOfGenesToReplace);
        for (int i = 0; i < crossoverData.list1.size()-1; i++){
            if (random.nextInt(0, crossoverData.list1.size()) < numberOfGenesToReplace){
                Task tempTask = crossoverData.list1.get(i);
                crossoverData.list1.set(i, crossoverData.list2.get(i));
                crossoverData.list2.set(i, tempTask);
            }
        }

        ArrayList<Task> mutatedList1 = mutation.mutate(crossoverData.list1);
        ArrayList<Task> mutatedList2 = mutation.mutate(crossoverData.list1);
        ArrayList<Task> mutatedList3 = mutation.mutate(crossoverData.list2);
        ArrayList<Task> mutatedList4 = mutation.mutate(crossoverData.list2);

        return new CrossoverAndMutationData(crossoverData.list1, crossoverData.list2, mutatedList1, mutatedList2, mutatedList3, mutatedList4);
    }
}
