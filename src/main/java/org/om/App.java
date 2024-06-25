package org.om;

import lombok.SneakyThrows;
import org.om.ga.Genotype;
import org.om.ga.Stats;
import org.om.graph.FitnessValues;
import org.om.graph.SimulationInstance;

public class App {
    @SneakyThrows
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
        Manager manager = new Manager(fitnessValues);
        Genotype bestA = manager.simulateMultipleDaysPerIter(30, 200, 1000);
        Genotype bestB = manager.simulateOneByOne(30, 100, 100);

        System.out.println("Implementation A");
        SimulationInstance simulationInstanceA = new SimulationInstance(manager.getStartingGraph().clone(), 0d, new FitnessValues());
        simulationInstanceA.simulateDays(60, bestA.getTasks());
        printInfo(simulationInstanceA.getStats());

        System.out.println("\nImplementation B");
        SimulationInstance simulationInstanceB = new SimulationInstance(manager.getStartingGraph().clone(), 0d, new FitnessValues());
        simulationInstanceB.simulateDays(60, bestB.getTasks());
        printInfo(simulationInstanceB.getStats());
    }

    private static void printInfo(Stats stats) {
        System.out.println("Sold price: " + stats.getSoldPrice());
        System.out.println("Orders failed: " + stats.getOrdersFailed());
        System.out.println("Storage cost: " + stats.getStorageCost());
        System.out.println("Delivery cost: " + stats.getDeliveryCost());
    }
}
