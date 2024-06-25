package org.om.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.om.ga.crossover.Crossover;
import org.om.ga.crossover.UniformCrossover;
import org.om.ga.mutation.Mutation;
import org.om.ga.mutation.UniformMutation;
import org.om.ga.selection.Selection;
import org.om.ga.selection.TournamentSelection;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Population {
    private List<Genotype> genotypes = new ArrayList<>();
    private Selection selection = new TournamentSelection();
    private Crossover crossover = new UniformCrossover();
    private Mutation mutation = new UniformMutation();

    public Integer getSize() {
        return genotypes.size();
    }

    public List<Genotype> getRandomGenotypes(Integer num) {
        List<Genotype> newGenotypes = new ArrayList<>(num);
        Set<Integer> taken = new HashSet<>(num);
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            while (true) {
                Integer index = random.nextInt(0, genotypes.size());
                if (!taken.contains(index)) {
                    newGenotypes.add(genotypes.get(index));
                    taken.add(index);
                    break;
                }
            }
        }
        return newGenotypes;
    }

    public List<Genotype> getRandomGenotypesWithCrossover(Integer num) {
        List<Genotype> newGenotypes = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // selection two parents
            Genotype parent1 = selection.select(this);
            Genotype parent2 = selection.select(this);

            // apply crossover
            Genotype offspring = crossover.cross(parent1, parent2);

            newGenotypes.add(offspring);
        }
        return newGenotypes;
    }

    public List<Genotype> getRandomGenotypesWithMutation(Integer num) {
        List<Genotype> newGenotypes = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // selection two parents
            Genotype parent1 = selection.select(this);
            Genotype parent2 = selection.select(this);

            // apply crossover
            Genotype offspring = crossover.cross(parent1, parent2);

            // apply mutation
            Genotype mutated = mutation.mutate(offspring);

            newGenotypes.add(mutated);
        }
        return newGenotypes;
    }

    public Genotype getBestGenotype() {
        Genotype best = genotypes.get(0);
        for (Genotype genotype : genotypes) {
            if (genotype.getFitness() > best.getFitness()) {
                best = genotype;
            }
        }
        return best;
    }
}
