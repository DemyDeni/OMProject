package org.om.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.om.ga.Stats;
import org.om.ga.Task;

import java.util.*;

@AllArgsConstructor
@Getter
@Setter
public class SimulationInstance {

    private final Graph graph;
    private double fitness;
    private FitnessValues fitnessValues;
    private final Random random = new Random();
    private final Integer pricePerUnitOfDistance = 2;
    private final Integer pricePerStorage = 1;
    private final Integer pricePerItemLost = 15;
    private final Integer pricePerItemSold = 30;
    private final Stats stats = new Stats();

    public double simulateDays(Integer days, List<Task> tasks) {
        for (int i = 0; i < days; i++) {
            payStorageCost();
            handleOrders();
            graph.getManufacturer().generateNewItems();
            runGA(tasks);
        }
        return fitness;
    }

    public void payStorageCost() {
        for (Map.Entry<Item, Integer> entry : this.graph.getManufacturer().getItems().entrySet()) {
            fitness -= entry.getValue() * fitnessValues.getManufacturerStorageCost() * entry.getKey().getStorageMultiplier() * pricePerStorage;
        }
        for (Distributor distributor : graph.getDistributors()) {
            for (Map.Entry<Item, Integer> entry : distributor.getItems().entrySet()) {
                fitness -= entry.getValue() * fitnessValues.getDistributorStorageCost() * entry.getKey().getStorageMultiplier() * pricePerStorage;
                stats.storageCost += entry.getValue() * fitnessValues.getDistributorStorageCost() * entry.getKey().getStorageMultiplier();
            }
        }
        for (Retailer retailer : graph.getRetailers()) {
            for (Map.Entry<Item, Integer> entry : retailer.getItems().entrySet()) {
                fitness -= entry.getValue() * fitnessValues.getRetailerStorageCost() * entry.getKey().getStorageMultiplier() * pricePerStorage;
                stats.storageCost += entry.getValue() * fitnessValues.getRetailerStorageCost() * entry.getKey().getStorageMultiplier();
            }
        }
    }

    public void handleOrders() {
        int i = 0;
        for (Person person : graph.getPersons()) {
            HashMap<Item, Integer> orders = person.generateNewOrders();
            for (Map.Entry<Item, Integer> order : orders.entrySet()) {
                if (order.getValue() > 0) {
                    sellItems(order, i);
                }
            }
            for (Map.Entry<Item, Integer> entry : orders.entrySet()) {
                if (entry.getValue() > 0) {
                    fitness -= entry.getKey().getPrice() * entry.getValue() * pricePerItemLost;
                    stats.ordersFailed += entry.getValue();
                }
            }
            i++;
        }
    }

    public void sellItems(Map.Entry<Item, Integer> order, int i) {
        for (Retailer retailer : graph.getRetailers()) {
            if (retailer.items.get(order.getKey()) > order.getValue()) {
                fitness += order.getValue() * order.getKey().getPrice() * pricePerItemSold;
                stats.soldPrice += order.getValue() * order.getKey().getPrice();

                fitness -= retailer.personDistances.get(i) * pricePerUnitOfDistance;
                stats.deliveryCost += retailer.personDistances.get(i);

                retailer.items.put(order.getKey(), retailer.items.get(order.getKey()) - order.getValue());
                order.setValue(0);
            } else if (retailer.items.get(order.getKey()) > 0) {
                fitness += retailer.items.get(order.getKey()) * order.getKey().getPrice() * pricePerItemSold;
                stats.soldPrice += retailer.items.get(order.getKey()) * order.getKey().getPrice();

                fitness -= retailer.personDistances.get(i) * pricePerUnitOfDistance;
                stats.deliveryCost += retailer.personDistances.get(i);

                order.setValue(order.getValue() - retailer.items.get(order.getKey()));
                retailer.items.put(order.getKey(), 0);
            }
        }
    }

    public void runGA(List<Task> tasks) {

        for (Task task : tasks) {
            if (random.nextDouble(0, 1d) < task.getChance()) {
                ArrayList<Distributor> distributors = graph.getDistributors();
                ArrayList<Retailer> retailers = graph.getRetailers();
                Manufacturer manufacturer = graph.getManufacturer();

                if (task.getFromType() == StorageType.DISTRIBUTOR) {
                    HashMap<Item, Integer> distributorFromItems = distributors.get(task.getFrom()).items;
                    HashMap<Item, Integer> retailerToItems = retailers.get(task.getTo()).items;
                    Integer distance = distributors.get(task.getFrom()).retailerDistances.get(task.getTo());

                    if (distributorFromItems.get(task.getItem()) > task.getItemNum()) {
                        distributorFromItems.put(task.getItem(), distributorFromItems.get(task.getItem()) - task.getItemNum());
                        retailerToItems.put(task.getItem(), retailerToItems.get(task.getItem()) + task.getItemNum());

                        fitness -= distance * pricePerUnitOfDistance * task.getItemNum();
                        stats.deliveryCost += distance * task.getItemNum();
                    } else if (distributorFromItems.get(task.getItem()) != 0) {
                        retailerToItems.put(task.getItem(), retailerToItems.get(task.getItem()) + distributorFromItems.get(task.getItem()));

                        fitness -= distance * pricePerUnitOfDistance * distributorFromItems.get(task.getItem());
                        stats.deliveryCost += distance * distributorFromItems.get(task.getItem());

                        distributorFromItems.put(task.getItem(), 0);
                    }
                } else {
                    HashMap<Item, Integer> manufacturerItems = manufacturer.getItems();
                    HashMap<Item, Integer> distributorToItems = graph.getDistributors().get(task.getTo()).items;
                    Integer distance = manufacturer.getDistributorDistances().get(task.getTo());

                    if (manufacturerItems.get(task.getItem()) > task.getItemNum()) {
                        manufacturerItems.put(task.getItem(), manufacturerItems.get(task.getItem()) - task.getItemNum());
                        distributorToItems.put(task.getItem(), distributorToItems.get(task.getItem()) + task.getItemNum());

                        fitness -= distance * pricePerUnitOfDistance * task.getItemNum();
                        stats.deliveryCost += distance * distributorToItems.get(task.getItem());
                    } else if (manufacturerItems.get(task.getItem()) != 0) {
                        graph.getDistributors().get(task.getTo()).items.put(task.getItem(), graph.getDistributors().get(task.getTo()).items.get(task.getItem()) + graph.getManufacturer().getItems().get(task.getItem()));

                        fitness -= distance * pricePerUnitOfDistance * manufacturerItems.get(task.getItem());
                        stats.deliveryCost += distance * graph.getManufacturer().getItems().get(task.getItem());

                        graph.getManufacturer().getItems().put(task.getItem(), 0);
                    }
                }
            }
        }
    }
}
