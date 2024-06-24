package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;

@Getter
@Setter
public class Manufacturer implements Storage, Cloneable {
    Integer storageCost = 1;
    HashMap<Item, Integer> items;
    ArrayList<Integer> distributorDistances;

    HashMap<Item, Integer> itemsToProduce;

    public Manufacturer(List<Item> itemList, List<Distributor> distributorList, HashMap<Item, Integer> itemsToProduceList) {
        itemsToProduce = itemsToProduceList;
        items = new HashMap<>(itemList.size());
        for (Item item : itemList) {
            items.put(item, 0);
        }

        Random random = new Random();
        distributorDistances = new ArrayList<>(distributorList.size());
        for (Distributor item : distributorList) {
            distributorDistances.add(random.nextInt(1, 20)); //TODO
        }
    }

    public void generateNewItems() {
        for (Map.Entry<Item, Integer> i : itemsToProduce.entrySet()) {
            items.put(i.getKey(), items.get(i.getKey()) + i.getValue());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manufacturer that = (Manufacturer) o;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageCost, items, distributorDistances, itemsToProduce);
    }

    @SneakyThrows
    public Manufacturer clone() {
        Manufacturer cloned = (Manufacturer) super.clone();
        cloned.items = new HashMap<>(this.items.size());
        for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
            cloned.items.put(entry.getKey().clone(), entry.getValue());
        }
        return cloned;
    }
}
