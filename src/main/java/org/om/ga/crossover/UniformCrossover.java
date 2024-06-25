package org.om.ga.crossover;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.om.ga.Genotype;

import java.util.Random;

@NoArgsConstructor
@AllArgsConstructor
public class UniformCrossover implements Crossover {
    private Double percentageOfGenesToReplace = 0.04;

    @Override
    public Genotype cross(Genotype parent1, Genotype parent2) {
        Random random = new Random();
        int numberOfGenesToReplace = (int) (parent1.getTasks().size() * percentageOfGenesToReplace);
        for (int i = 0; i < parent1.getTasks().size() - 1; i++) {
            if (random.nextInt(0, parent1.getTasks().size()) < numberOfGenesToReplace) {
                parent1.getTasks().set(i, parent2.getTasks().get(i));
            }
        }

        return parent1;
    }
}
