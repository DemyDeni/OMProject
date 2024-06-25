package org.om.ga.selection;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.om.ga.Genotype;
import org.om.ga.Population;

import java.util.Random;

@Setter
@NoArgsConstructor
public class TournamentSelection implements Selection {
    private Random random = new Random();
    private Integer poolSize = 4;

    @Override
    public Genotype select(Population population) {
        Genotype best = population.getGenotypes().get(random.nextInt(population.getSize()));

        for (int i = 1; i < poolSize; i++) {
            Genotype competitor = population.getGenotypes().get(random.nextInt(population.getSize()));
            if (competitor.getFitness() > best.getFitness()) {
                best = competitor;
            }
        }

        return best;
    }
}