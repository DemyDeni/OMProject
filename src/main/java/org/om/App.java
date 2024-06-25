package org.om;

import org.om.ga.Genotype;
import org.om.graph.FitnessValues;

public class App {
    public static void main(String[] args) {
        FitnessValues fitnessValues = FitnessValues.builder()
                .moveItemNum(10)
                .moveItemChance(0.15d)
                .itemPriceMod(10d)
                .manufacturerStorageCost(1d)
                .distributorStorageCost(2d)
                .retailerStorageCost(3d)
                .moveItemMod(0.1d)
                .noAvailableItemsToTakeMod(0.05d)
                .availableItemsToTakeMod(1d)
                .build();
        Manager manager = new Manager(100, fitnessValues);
        Genotype best = manager.simulateOneByOne(30, 500);


        Manager manager = new Manager();
//        Genotype best = manager.simulateOneByOne(10, 100, 100);
        Genotype best2 = manager.simulateMultipleDaysPerIter(30, 200, 1000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
