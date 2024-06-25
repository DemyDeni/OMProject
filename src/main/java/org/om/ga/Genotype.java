package org.om.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.om.graph.Distributor;
import org.om.graph.Graph;
import org.om.graph.Item;
import org.om.graph.Retailer;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Genotype {
    private Double fitness = 0d;
    private List<Task> tasks = new ArrayList<>();

    public static Genotype generateRandomGenotype(Graph graph, Integer moveItemNum, Double moveItemChance) {
        Genotype genotype = new Genotype();
        for (Distributor distributor : graph.getDistributors()) {
            // add all links from manufacturer to all distributors
            for (Item item : graph.getItems()) {
                genotype.tasks.add(new Task(graph.getManufacturer(), distributor, item, moveItemNum, moveItemChance));
            }
            // add all links from all distributors to all retailers
            for (Retailer retailer : graph.getRetailers()) {
                for (Item item : graph.getItems()) {
                    genotype.tasks.add(new Task(distributor, retailer, item, moveItemNum, moveItemChance));
                }
            }
        }
        return genotype;
    }
}
