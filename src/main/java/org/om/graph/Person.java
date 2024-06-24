package org.om.graph;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Person implements Storage, Cloneable{
    ArrayList<Double> itemProbabilities;
    int minOrders;
    int maxOrders;
    HashMap<Item, Integer> orders;

    public Person(Integer minOrders, Integer maxOrders, List<Item> items) {
        this.minOrders = minOrders;
        this.maxOrders = maxOrders;
        orders = new HashMap<>();

        //TODO: generate probs
        Random random = new Random();
        itemProbabilities = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            itemProbabilities.add(random.nextDouble(0, 0.15));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemProbabilities, minOrders, maxOrders, orders);
    }

    public HashMap<Item, Integer> generateNewOrders(List<Item> items) {
        Random random = new Random();
        HashMap<Item,Integer> newOrders = new HashMap<>(items.size());
        for (int i = 0; i < itemProbabilities.size(); i++) {
            if (random.nextInt(0, 101) < itemProbabilities.get(i)) {
                newOrders.put(items.get(i), random.nextInt(minOrders, maxOrders));
            }
        }
        return newOrders;
    }
}
