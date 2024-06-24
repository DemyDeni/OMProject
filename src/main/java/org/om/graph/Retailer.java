package org.om.graph;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Retailer implements Storage {
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
            personDistances.add(random.nextInt(1, 20)); //TODO
        }
    }
}
