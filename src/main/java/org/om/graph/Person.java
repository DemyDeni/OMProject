package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;

@Getter
@Setter
public class Person implements Cloneable {
    HashMap<Item, Double> itemProbabilities;
    Integer minOrders;
    Integer maxOrders;
    HashMap<Item, Integer> orders;

    public Person(Integer minOrders, Integer maxOrders, List<Item> items, ArrayList<Double> itemProbabilitiesInput) {
        this.minOrders = minOrders;
        this.maxOrders = maxOrders;
        orders = new HashMap<>();

        int i = 0;
        itemProbabilities = new HashMap<Item, Double>();
        for (Item item : items) {
            itemProbabilities.put(item, itemProbabilitiesInput.get(i));
            i++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemProbabilities, minOrders, maxOrders, orders);
    }

    @SneakyThrows
    public Person clone() {
        Person cloned = (Person) super.clone();
        cloned.itemProbabilities = new HashMap<>(this.itemProbabilities.size());
        for (Map.Entry<Item, Double> entry : this.itemProbabilities.entrySet()) {
            cloned.itemProbabilities.put(entry.getKey().clone(), entry.getValue());
        }
        cloned.orders = new HashMap<>(this.orders.size());
        for (Map.Entry<Item, Integer> entry : this.orders.entrySet()) {
            cloned.orders.put(entry.getKey().clone(), entry.getValue());
        }
        return cloned;
    }

    public HashMap<Item, Integer> generateNewOrders() {
        Random random = new Random();
        HashMap<Item, Integer> newOrders = new HashMap<>(itemProbabilities.size());
        for (Map.Entry<Item, Double> entry : itemProbabilities.entrySet()) {
            if (random.nextDouble(0, 1) < entry.getValue()) {
                newOrders.put(entry.getKey(), random.nextInt(minOrders, maxOrders));
            }
        }
        return newOrders;
    }
}
