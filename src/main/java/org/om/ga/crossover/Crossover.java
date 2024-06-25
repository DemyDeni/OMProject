package org.om.ga.crossover;

import org.om.ga.Genotype;

public interface Crossover {
    Genotype cross(Genotype parent1, Genotype parent2);
}
