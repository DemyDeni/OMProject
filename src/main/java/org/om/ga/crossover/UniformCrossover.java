package org.om.ga.crossover;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.om.ga.Genotype;

import java.util.Random;

@Setter
@NoArgsConstructor
public class UniformCrossover implements Crossover {
    private Random random = new Random();
    private Double percentageOfGenesToReplace = 0.04;

    @Override
    public Genotype cross(Genotype parent1, Genotype parent2) {
        int numberOfGenesToReplace = (int) (parent1.getTasks().size() * percentageOfGenesToReplace);
        Genotype newGenotype = parent1.clone();
        for (int i = 0; i < parent1.getTasks().size() - 1; i++) {
            if (random.nextInt(0, parent1.getTasks().size()) < numberOfGenesToReplace) {
                newGenotype.getTasks().set(i, parent2.getTasks().get(i));
            }
        }
        return newGenotype;
    }
}
