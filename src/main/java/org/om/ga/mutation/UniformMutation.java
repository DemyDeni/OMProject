package org.om.ga.mutation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.om.ga.Genotype;
import org.om.ga.Task;

import java.util.Random;

@NoArgsConstructor
@AllArgsConstructor
public class UniformMutation implements Mutation {
    private Double mutationChance = 0.01;

    @Override
    public Genotype mutate(Genotype genotype) {
        Random random = new Random();
        for (Task task : genotype.getTasks()) {
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
        return genotype;
    }
}
