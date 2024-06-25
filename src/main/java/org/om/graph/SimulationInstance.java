package org.om.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.om.ga.Stats;
import org.om.ga.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            for (Map.Entry<Item, Integer> entry : orders.entrySet()) {
                if (entry.getValue() > 0) {
                    for (Retailer retailer : graph.getRetailers()) {
                        if (retailer.items.get(entry.getKey()) > entry.getValue()) {
                            fitness += entry.getValue() * entry.getKey().getPrice() * pricePerItemSold;
                            stats.soldPrice += entry.getValue() * entry.getKey().getPrice();
                            fitness -= retailer.personDistances.get(i) * pricePerUnitOfDistance;
                            stats.deliveryCost += retailer.personDistances.get(i);

                            retailer.items.put(entry.getKey(), retailer.items.get(entry.getKey()) - entry.getValue());
                            entry.setValue(0);
                        } else if (retailer.items.get(entry.getKey()) > 0) {
                            fitness += retailer.items.get(entry.getKey()) * entry.getKey().getPrice() * pricePerItemSold;
                            stats.soldPrice += retailer.items.get(entry.getKey()) * entry.getKey().getPrice();
                            fitness -= retailer.personDistances.get(i) * pricePerUnitOfDistance;
                            stats.deliveryCost += retailer.personDistances.get(i);

                            entry.setValue(entry.getValue() - retailer.items.get(entry.getKey()));
                            retailer.items.put(entry.getKey(), 0);
                        }
                    }
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

    public void runGA(List<Task> tasks) {
        for (Task task : tasks) {
            if (random.nextDouble(0, 1d) < task.getChance()) {
                if (task.getFromType() == StorageType.DISTRIBUTOR) {
                    if (graph.getDistributors().get(task.getFrom()).items.get(task.getItem()) > task.getItemNum()) {
                        graph.getDistributors().get(task.getFrom()).items.put(task.getItem(), graph.getDistributors().get(task.getFrom()).items.get(task.getItem()) - task.getItemNum());
                        graph.getRetailers().get(task.getTo()).items.put(task.getItem(), graph.getRetailers().get(task.getTo()).items.get(task.getItem()) + task.getItemNum());

                        fitness -= graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * pricePerUnitOfDistance * task.getItemNum();
                        stats.deliveryCost += graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * task.getItemNum();
                    } else if (graph.getDistributors().get(task.getFrom()).items.get(task.getItem()) > 0) {
                        graph.getRetailers().get(task.getTo()).items.put(task.getItem(), graph.getRetailers().get(task.getTo()).items.get(task.getItem()) + graph.getDistributors().get(task.getFrom()).items.get(task.getItem()));

                        fitness -= graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * pricePerUnitOfDistance * graph.getDistributors().get(task.getFrom()).items.get(task.getItem());
                        stats.deliveryCost += graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * graph.getDistributors().get(task.getFrom()).items.get(task.getItem());

                        graph.getDistributors().get(task.getFrom()).items.put(task.getItem(), 0);
                    }
                } else {
                    if (graph.getManufacturer().getItems().get(task.getItem()) > task.getItemNum()) {
                        graph.getManufacturer().getItems().put(task.getItem(), graph.getManufacturer().getItems().get(task.getItem()) - task.getItemNum());
                        graph.getDistributors().get(task.getTo()).items.put(task.getItem(), graph.getDistributors().get(task.getTo()).items.get(task.getItem()) + task.getItemNum());

                        fitness -= graph.getManufacturer().getDistributorDistances().get(task.getTo()) * pricePerUnitOfDistance * task.getItemNum();
                        stats.deliveryCost += graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * graph.getDistributors().get(task.getFrom()).items.get(task.getItem());
                    } else if (graph.getManufacturer().getItems().get(task.getItem()) > 0) {
                        graph.getDistributors().get(task.getTo()).items.put(task.getItem(), graph.getDistributors().get(task.getTo()).items.get(task.getItem()) + graph.getManufacturer().getItems().get(task.getItem()));

                        fitness -= graph.getManufacturer().getDistributorDistances().get(task.getTo()) * pricePerUnitOfDistance * graph.getManufacturer().getItems().get(task.getItem());
                        stats.deliveryCost += graph.getManufacturer().getDistributorDistances().get(task.getTo()) * graph.getManufacturer().getItems().get(task.getItem());

                        graph.getManufacturer().getItems().put(task.getItem(), 0);
                    }
                }
            }
        }
    }
}
