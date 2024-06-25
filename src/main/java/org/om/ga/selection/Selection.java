package org.om.ga.selection;

import org.om.ga.Genotype;
import org.om.ga.Population;

public interface Selection {
    Genotype select(Population population);
}
