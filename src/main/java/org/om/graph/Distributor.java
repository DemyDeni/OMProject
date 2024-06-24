package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;

@Getter
@Setter
public class Distributor implements Storage, Cloneable {
    Integer storageCost = 2;
    HashMap<Item, Integer> items;
    ArrayList<Integer> retailerDistances;

    public Distributor(List<Item> itemList, List<Retailer> retailerList) {
        items = new HashMap<>(itemList.size());
        for (Item item : itemList) {
            items.put(item, 0);
        }

        Random random = new Random();
        retailerDistances = new ArrayList<>(retailerList.size());
        for (Retailer retailer : retailerList) {
            retailerDistances.add(random.nextInt(10, 100));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distributor that = (Distributor) o;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(storageCost, items, retailerDistances);
    }

    @SneakyThrows
    public Distributor clone() {
        Distributor cloned = (Distributor) super.clone();
        cloned.items = new HashMap<>(this.items.size());
        for (Map.Entry<Item, Integer> entry : this.items.entrySet()) {
            cloned.items.put(entry.getKey().clone(), entry.getValue());
        }
        return cloned;
    }
}
