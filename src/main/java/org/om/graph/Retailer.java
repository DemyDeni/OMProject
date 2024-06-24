package org.om.graph;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;

@Getter
public class Retailer implements Storage, Cloneable {
    Integer storageCost = 3;
    HashMap<Item, Integer> items;
    ArrayList<Integer> personDistances;

    public Retailer(List<Item> itemList, List<Person> personList) {
        items = new HashMap<>(itemList.size());
        for (Item item : itemList) {
            items.put(item, 0);
        }

        Random random = new Random();
        personDistances = new ArrayList<>(personList.size());
        for (Person person : personList) {
            personDistances.add(random.nextInt(10, 100));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Retailer retailer = (Retailer) o;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageCost, items, personDistances);
    }

    @SneakyThrows
    public Retailer clone() {
        Retailer cloned = (Retailer) super.clone();
        cloned.items = new HashMap<>(this.items.size());
        for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
            cloned.items.put(entry.getKey().clone(), entry.getValue());
        }
        return cloned;
    }
}
