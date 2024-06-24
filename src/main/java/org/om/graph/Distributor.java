package org.om.graph;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Distributor implements Storage {
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
            retailerDistances.add(random.nextInt(1, 20)); //TODO
        }
    }
}
