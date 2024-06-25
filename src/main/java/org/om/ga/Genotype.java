package org.om.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.om.graph.*;

import java.util.*;

@Getter
@NoArgsConstructor
public class Genotype implements Cloneable {
    @Setter
    private Double fitness = 0d;
    private List<Task> tasks = new ArrayList<>();

    public static Genotype generateRandomGenotype(Graph graph, FitnessValues fitnessValues) {
        Genotype genotype = new Genotype();
        for (int d = 0; d < graph.getDistributors().size(); d++) {
            // add all links from manufacturer to all distributors
            for (Item item : graph.getItems()) {
                genotype.tasks.add(new Task(StorageType.MANUFACTURER, 0, d, item, fitnessValues.getMoveItemNum(), fitnessValues.getMoveItemChance()));
            }
            // add all links from all distributors to all retailers
            for (int r = 0; r < graph.getRetailers().size(); r++) {
                for (Item item : graph.getItems()) {
                    genotype.tasks.add(new Task(StorageType.DISTRIBUTOR, d, r, item, fitnessValues.getMoveItemNum(), fitnessValues.getMoveItemChance()));
                }
            }
        }
        genotype.fitness = genotype.calculateFitness(graph, fitnessValues);
        return genotype;
    }

    public Double calculateFitness(Graph graph, FitnessValues fitnessValues) {
        // iterate over manufacturer items
        Double newFitness = calculateFitnessForItems(graph.getManufacturer().getItems(), fitnessValues);
        // iterate over all items in all distributors
        for (Distributor distributor : graph.getDistributors()) {
            newFitness += calculateFitnessForItems(distributor.getItems(), fitnessValues);
        }
        // iterate over all items in all retailers
        for (Retailer retailer : graph.getRetailers()) {
            newFitness += calculateFitnessForItems(retailer.getItems(), fitnessValues);
        }
        return newFitness;
    }

    private Double calculateFitnessForItems(HashMap<Item, Integer> items, FitnessValues fitnessValues) {
        double fitness = 0d;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            fitness += entry.getKey().getPrice() * fitnessValues.getItemPriceMod() * entry.getValue();
        }
        return fitness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(fitness, tasks);
    }

    @SneakyThrows
    public Genotype clone() {
        Genotype cloned = (Genotype) super.clone();
        cloned.tasks = new ArrayList<>(this.tasks.size());
        for (Task task : this.tasks) {
            cloned.tasks.add(task.clone());
        }
        return cloned;
    }
}
