package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

@Getter
@Setter
public class Graph implements Cloneable {
    private ArrayList<Item> items;
    private ArrayList<Person> persons;
    private ArrayList<Retailer> retailers;
    private ArrayList<Distributor> distributors;
    private Manufacturer manufacturer;

    public Graph(GraphConfig config) {
        generateItems(config.getItemsNum(), config.getItemMinPrice(), config.getItemMaxPrice(),
                config.getItemMinMultiplier(), config.getItemMaxMultiplier());
        generatePersons(config.getPersonsNum(), config.getPersonsMinOrders(), config.getPersonsMaxOrders());
        generateRetailers(config.getRetailersNum());
        generateDistributors(config.getDistributorsNum());
        generateManufacturer(config.getManufacturerItemsPerDay());
    }

    private void generateItems(Integer num, Integer minPrice, Integer maxPrice, Double minMultiplier, Double maxMultiplier) {
        items = new ArrayList<>(num);
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            items.add(new Item(i, random.nextInt(minPrice, maxPrice), random.nextDouble(minMultiplier, maxMultiplier)));
        }
    }

    private void generatePersons(Integer num, Integer minOrders, Integer maxOrders) {
        persons = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            persons.add(new Person(minOrders, maxOrders, items));
        }
    }

    private void generateRetailers(Integer num) {
        retailers = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            retailers.add(new Retailer(items, persons));
        }
    }

    private void generateDistributors(Integer num) {
        distributors = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            distributors.add(new Distributor(items, retailers));
        }
    }

    private void generateManufacturer(Integer newItemNum) {
        HashMap<Item, Integer> itemsToProduce = new HashMap<>(items.size());
        for (Item item : items) {
            itemsToProduce.put(item, newItemNum / items.size()); // TODO: generate
        }
        manufacturer = new Manufacturer(items, distributors, itemsToProduce);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, persons, retailers, distributors, manufacturer);
    }

    @SneakyThrows
    public Graph clone() {
        Graph cloned = (Graph) super.clone();
        cloned.persons = new ArrayList<>(this.persons.size());
        for (Person person : this.persons) {
            cloned.persons.add(person.clone());
        }
        cloned.retailers = new ArrayList<>(this.retailers.size());
        for (Retailer retailer : this.retailers) {
            cloned.retailers.add(retailer.clone());
        }
        cloned.distributors = new ArrayList<>(this.distributors.size());
        for (Distributor distributor : this.distributors) {
            cloned.distributors.add(distributor.clone());
        }
        cloned.manufacturer = this.manufacturer.clone();
        return cloned;
    }
}
