package org.om.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.om.ga.Genotype;
import org.om.ga.Task;

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
            personToAdd.setOrders(personToAdd.generateNewOrders());
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

    public void applyGenotype(Genotype genotype, FitnessValues fitnessValues) {
        Double fitness = genotype.calculateFitness(this, fitnessValues);
        fitness -= calculateStorageCost(genotype, fitnessValues);
        fitness -= moveAndCalculateMoveCost(genotype.getTasks(), fitnessValues);
        fitness -= takeAndCalculateTakeCost(fitnessValues);
        genotype.setFitness(fitness);
    }

    private Double calculateStorageCost(Genotype genotype, FitnessValues fitnessValues) {
        // iterate over all items in all manufacturer
        Double fitnessPenalty = calculateStorageCostForItems(StorageType.MANUFACTURER, manufacturer.getItems(), fitnessValues);
        // iterate over all items in all distributors
        for (Distributor distributor : distributors) {
            fitnessPenalty += calculateStorageCostForItems(StorageType.DISTRIBUTOR, distributor.getItems(), fitnessValues);
        }
        // iterate over all items in all retailers
        for (Retailer retailer : retailers) {
            fitnessPenalty += calculateStorageCostForItems(StorageType.RETAILER, retailer.getItems(), fitnessValues);
        }
        return fitnessPenalty;
    }

    private Double calculateStorageCostForItems(StorageType storageType, HashMap<Item, Integer> items, FitnessValues fitnessValues) {
        Double fitnessPenalty = 0d;
        double storageCost = getStorageCost(storageType, fitnessValues);
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            fitnessPenalty += storageCost * entry.getKey().getStorageMultiplier();
        }
        return fitnessPenalty;
    }

    private Double getStorageCost(StorageType storageType, FitnessValues fitnessValues) {
        switch (storageType) {
            case MANUFACTURER -> {
                return fitnessValues.getManufacturerStorageCost();
            }
            case DISTRIBUTOR -> {
                return fitnessValues.getDistributorStorageCost();
            }
            case RETAILER -> {
                return fitnessValues.getRetailerStorageCost();
            }
            default -> {
                return 1d;
            }
        }
    }

    private Double moveAndCalculateMoveCost(List<Task> tasks, FitnessValues fitnessValues) {
        Double fitnessPenalty = 0d;
        for (Task task : tasks) {
            Item item = task.getItem();
            Integer itemNum = task.getItemNum();
            if (task.getFromType() == StorageType.MANUFACTURER) {
                Manufacturer from = manufacturer;
                Distributor to = distributors.get(task.getTo());
                if (from.getItems().get(item) < itemNum) {
                    int itemsMissing = itemNum - from.getItems().get(item);
                    fitnessPenalty += itemsMissing * fitnessValues.getNoAvailableItemsToMoveMod();
                    from.getItems().put(item, 0);
                    to.getItems().put(item, to.getItems().get(item) + itemNum - itemsMissing);
                } else {
                    from.getItems().put(item, from.getItems().get(item) - itemNum);
                    to.getItems().put(item, to.getItems().get(item) + itemNum);
                }
                fitnessPenalty += manufacturer.getDistributorDistances().get(task.getTo());
            } else {
                Distributor from = distributors.get(task.getFrom());
                Retailer to = retailers.get(task.getTo());
                if (from.getItems().get(item) < itemNum) {
                    int itemsMissing = itemNum - from.getItems().get(item);
                    fitnessPenalty += itemsMissing * fitnessValues.getNoAvailableItemsToMoveMod();
                    from.getItems().put(item, 0);
                    to.getItems().put(item, to.getItems().get(item) + itemNum - itemsMissing);
                } else {
                    from.getItems().put(item, from.getItems().get(item) - itemNum);
                    to.getItems().put(item, to.getItems().get(item) + itemNum);
                }
                fitnessPenalty += from.getRetailerDistances().get(task.getTo());
            }
        }
        return fitnessPenalty;
    }

    private Double takeAndCalculateTakeCost(FitnessValues fitnessValues) {
        Double fitnessPenalty = 0d;
        for (Person person : persons) {
            for (Map.Entry<Item, Integer> order : person.getOrders().entrySet()) {
                if (order.getValue() == 0) continue;
                int itemsLeft = order.getValue();
                for (Retailer retailer : retailers) {
                    int itemsInRetailer = retailer.getItems().get(order.getKey());
                    int itemsToTake;
                    if (itemsInRetailer == 0) continue;
                    else if (itemsInRetailer >= order.getValue()) {
                        itemsToTake = itemsInRetailer - order.getValue();
                    } else {
                        itemsToTake = itemsInRetailer;
                    }
                    retailer.getItems().put(order.getKey(), itemsInRetailer - itemsToTake);
                    itemsLeft -= itemsToTake;
                    fitnessPenalty -= itemsToTake * fitnessValues.getAvailableItemsToTakeMod();

                    if (itemsLeft == 0) {
                        break;
                    } else {
                        fitnessPenalty += itemsLeft * fitnessValues.getNoAvailableItemsToTakeMod();
                    }
                }
            }
        }
        return fitnessPenalty;
    }
}
