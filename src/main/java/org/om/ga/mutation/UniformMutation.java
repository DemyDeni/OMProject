package org.om.ga.mutation;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.om.ga.Genotype;
import org.om.ga.Task;

import java.util.Random;

@Setter
@NoArgsConstructor
public class UniformMutation implements Mutation {
    private Random random = new Random();
    private Double mutationChance = 0.005;

    @Override
    public Genotype mutate(Genotype genotype) {
        Genotype newGenotype = genotype.clone();
        for (Task task : newGenotype.getTasks()) {
            if (random.nextDouble(0, 1) < mutationChance) {
                if (random.nextBoolean()) {
                    task.setChance(task.getChance() * 1.2);
                    task.setItemNum((int) (task.getItemNum() * 1.2));
                } else {
                    task.setChance(task.getChance() * 0.83333);
                    task.setItemNum((int) (task.getItemNum() * 0.83333));
                }
            }
        }
        return newGenotype;
    }
}
