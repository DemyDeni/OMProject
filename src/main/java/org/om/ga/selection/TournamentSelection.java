package org.om.ga.selection;

import org.om.ga.GA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TournamentSelection {
    private Random random = new Random();

    public ArrayList<GA> select(List<GA> population, double percentage) {
        int selectionSize = population.size()/2;
        ArrayList<GA> selected = new ArrayList<>();

        while (selected.size() < selectionSize) {
            selected.add(tournament(population));
        }

        return selected;
    }

    private GA tournament(List<GA> population) {
        GA best = population.get(random.nextInt(population.size()));

        for (int i = 1; i < 3; i++) {
            GA competitor = population.get(random.nextInt(population.size()));
            if (competitor.getFitness() > best.getFitness()) {
                best = competitor;
            }
        }

        return best;
    }
}