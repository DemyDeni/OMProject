package org.om.ga.mutation;

import org.om.ga.Genotype;

public interface Mutation {
    Genotype mutate(Genotype genotype);
}
