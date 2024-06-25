package org.om;

import org.om.ga.Genotype;
import org.om.graph.FitnessValues;

public class App {
    public static void main(String[] args) {
        FitnessValues fitnessValues = FitnessValues.builder()
                .moveItemNum(10)
                .moveItemChance(0.15d)
                .itemPriceMod(10d)
                .manufacturerStorageCost(0.1d)
                .distributorStorageCost(0.2d)
                .retailerStorageCost(0.3d)
                .noAvailableItemsToMoveMod(0.1d)
                .noAvailableItemsToTakeMod(0.05d)
                .availableItemsToTakeMod(1d)
                .build();
        Manager manager = new Manager(100, fitnessValues);
        Genotype best = manager.simulateOneByOne(50, 100);
    }
}
