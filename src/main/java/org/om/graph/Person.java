package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;

@Getter
@Setter
public class Person implements Storage, Cloneable {
    HashMap<Item, Double> itemProbabilities;
    Integer minOrders;
    Integer maxOrders;
    HashMap<Item, Integer> orders;

    public Person(Integer minOrders, Integer maxOrders, List<Item> items) {
        this.minOrders = minOrders;
        this.maxOrders = maxOrders;
        orders = new HashMap<>();

        //TODO: generate probs
        Random random = new Random();
        itemProbabilities = new HashMap<>(items.size());
        for (Item item : items) {
            itemProbabilities.put(item, random.nextDouble(0, 0.5));
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

    public HashMap<Item, Integer> generateNewOrders(List<Item> items) {
        Random random = new Random();
        HashMap<Item, Integer> newOrders = new HashMap<>(items.size());
        for (Item item : items) {
            if (random.nextDouble(0, 1) < itemProbabilities.get(item)) {
                newOrders.put(item, random.nextInt(minOrders, maxOrders));
            }
        }
        return newOrders;
    }
}
