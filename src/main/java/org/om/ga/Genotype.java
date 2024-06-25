package org.om.ga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.om.graph.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Genotype implements Cloneable {
    private Double fitness = 0d;
    private List<Task> tasks = new ArrayList<>();

    public static Genotype generateRandomGenotype(Graph graph, Integer moveItemNum, Double moveItemChance) {
        Genotype genotype = new Genotype();
        for (int i = 0; i < graph.getDistributors().size(); i++) {
            // add all links from manufacturer to all distributors
            for (Item item : graph.getItems()) {
                genotype.tasks.add(new Task(0, StorageType.MANUFACTURER, i, item, moveItemNum, moveItemChance));
            }
            // add all links from all distributors to all retailers
            for (int j = 0; j < graph.getRetailers().size(); j++) {
                for (Item item : graph.getItems()) {
                    genotype.tasks.add(new Task(i, StorageType.DISTRIBUTOR, j, item, moveItemNum, moveItemChance));
                }
            }
        }
        return genotype;
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
