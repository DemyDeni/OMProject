package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.om.ga.Genotype;

import java.util.*;

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
        generateManufacturer();
    }

    private void generateItems(Integer num, Integer minPrice, Integer maxPrice, Double minMultiplier, Double maxMultiplier) {
        items = new ArrayList<>(num);
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            items.add(new Item(i, random.nextInt(minPrice, maxPrice), random.nextDouble(minMultiplier, maxMultiplier)));
        }
    }

    private void generatePersons(Integer num, Integer minOrders, Integer maxOrders) {
        ArrayList<Double> itemProbabilitiesPool = new ArrayList<>();
        ArrayList<Integer> itemRequests = new ArrayList<>();
        Random random = new Random();

        double totalItemProb = 0.4 * num;
        for (int i = 0; i < items.size(); i++) {
            itemProbabilitiesPool.add(totalItemProb);
            itemRequests.add(0);
        }

        persons = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            ArrayList<Double> itemProbabilities = new ArrayList<>();
            double itemProb;
            for (int j = 0; j < itemProbabilitiesPool.size(); j++) {
                if (itemProbabilitiesPool.get(j) > 0.99) {
                    itemProb = random.nextDouble(0, 0.8);
                    itemProbabilities.add(itemProb);
                    itemProbabilitiesPool.set(j, itemProbabilitiesPool.get(j) - itemProb);
                } else {
                    itemProb = itemProbabilitiesPool.get(j);
                    itemProbabilities.add(itemProb);
                    itemProbabilitiesPool.set(j, itemProbabilitiesPool.get(j) - itemProb);
                }
            }
            Person personToAdd = new Person(minOrders, maxOrders, items, itemProbabilities);
            personToAdd.setOrders(personToAdd.generateNewOrders(items));
            persons.add(personToAdd);
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

    private void generateManufacturer() {
        HashMap<Item, Integer> itemsToProduce = new HashMap<>(items.size());
        for (Person person : persons) {
            for (Map.Entry<Item, Integer> entry : person.getOrders().entrySet()) {
                itemsToProduce.put(entry.getKey(), itemsToProduce.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        for (Map.Entry<Item, Integer> entry : itemsToProduce.entrySet()) {
            itemsToProduce.put(entry.getKey(), (int) (itemsToProduce.get(entry.getKey()) * 0.5));
        }
        manufacturer = new Manufacturer(items, distributors, itemsToProduce);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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

    public void applyPersonChoices(HashMap<Person, HashMap<Item, Integer>> choices) {
        for (Person person : persons) {
            person.setOrders(choices.get(person));
        }
    }

    public Genotype applyGenotype(Genotype genotypes) {
        //TODO: apply tasks
        //TODO: calculate fitness of genotype
        return null;
    }
}
