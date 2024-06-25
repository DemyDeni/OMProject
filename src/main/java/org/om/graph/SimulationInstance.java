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
    private final Random random = new Random();
    private final Integer pricePerUnitOfDistance = 2;
    private final Integer pricePerStorage = 1;
    private final Integer pricePerItemLost = 15;
    private final Integer pricePerItemSold = 30;
    private final Stats stats = new Stats();

    public double simulateDays(Integer days, List<Task> tasks){
        for (int i = 0; i < days; i++){
            payStorageCost();
            handleOrders();
            graph.getManufacturer().generateNewItems();
            runGA(tasks);
        }
        return fitness;
    }

    public void payStorageCost(){
        for (Map.Entry<Item, Integer> entry : this.graph.getManufacturer().getItems().entrySet()){
            fitness -= entry.getValue() * graph.getManufacturer().getStorageCost() * entry.getKey().storageMultiplier * pricePerStorage;
        }
        for (Distributor distributor : graph.getDistributors()){
            for (Map.Entry<Item, Integer> entry : distributor.getItems().entrySet()){
                fitness -= entry.getValue() * distributor.getStorageCost() * entry.getKey().storageMultiplier * pricePerStorage;
                stats.spentOnStorage += entry.getValue() * distributor.getStorageCost() * entry.getKey().storageMultiplier;
            }
        }
        for (Retailer retailer : graph.getRetailers()){
            for (Map.Entry<Item, Integer> entry : retailer.getItems().entrySet()){
                fitness -= entry.getValue() * retailer.getStorageCost() * entry.getKey().storageMultiplier * pricePerStorage;
                stats.spentOnStorage += entry.getValue() * retailer.getStorageCost() * entry.getKey().storageMultiplier;
            }
        }
    }

    public void handleOrders(){
        int i = 0;
        for (Person person : graph.getPersons()){
            HashMap<Item, Integer> orders = person.generateNewOrders();
            for (Map.Entry<Item, Integer> entry : orders.entrySet()){
                if (entry.getValue() > 0){
                    for (Retailer retailer : graph.getRetailers()){
                        if (retailer.items.get(entry.getKey()) > entry.getValue()) {
                            fitness += entry.getValue() * entry.getKey().price * pricePerItemSold;
                            stats.moneyEarned += entry.getValue() * entry.getKey().price;
                            fitness -= retailer.personDistances.get(i) * pricePerUnitOfDistance;
                            stats.spentOnDelivery += retailer.personDistances.get(i);

                            retailer.items.put(entry.getKey(), retailer.items.get(entry.getKey()) - entry.getValue());
                            entry.setValue(0);
                        }
                        else if (retailer.items.get(entry.getKey()) > 0) {
                            fitness += retailer.items.get(entry.getKey()) * entry.getKey().price * pricePerItemSold;
                            stats.moneyEarned += retailer.items.get(entry.getKey()) * entry.getKey().price;
                            fitness -= retailer.personDistances.get(i) * pricePerUnitOfDistance;
                            stats.spentOnDelivery += retailer.personDistances.get(i);

                            entry.setValue(entry.getValue() - retailer.items.get(entry.getKey()));
                            retailer.items.put(entry.getKey(), 0);
                        }
                    }
                }
            }
            for (Map.Entry<Item, Integer> entry : orders.entrySet()){
                if (entry.getValue() > 0) {
                    fitness -= entry.getKey().price * entry.getValue() * pricePerItemLost;
                    stats.ordersFailed += entry.getValue();
                }
            }
            i++;
        }
    }

    public void runGA(List<Task> tasks){
        for (Task task : tasks){
            if (random.nextDouble(0, 1d) < task.getChance()){
                if (task.getFromType() == StorageType.DISTRIBUTOR){
                    if (graph.getDistributors().get(task.getFrom()).items.get(task.getItem()) > task.getItemNum()){
                        graph.getDistributors().get(task.getFrom()).items.put(task.getItem(), graph.getDistributors().get(task.getFrom()).items.get(task.getItem()) - task.getItemNum());
                        graph.getRetailers().get(task.getTo()).items.put(task.getItem(), graph.getRetailers().get(task.getTo()).items.get(task.getItem()) + task.getItemNum());

                        fitness -= graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * pricePerUnitOfDistance * task.getItemNum();
                        stats.spentOnDelivery += graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * task.getItemNum();
                    }
                    else if (graph.getDistributors().get(task.getFrom()).items.get(task.getItem()) > 0){
                        graph.getRetailers().get(task.getTo()).items.put(task.getItem(), graph.getRetailers().get(task.getTo()).items.get(task.getItem()) + graph.getDistributors().get(task.getFrom()).items.get(task.getItem()));

                        fitness -= graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * pricePerUnitOfDistance * graph.getDistributors().get(task.getFrom()).items.get(task.getItem());
                        stats.spentOnDelivery +=graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * graph.getDistributors().get(task.getFrom()).items.get(task.getItem());

                        graph.getDistributors().get(task.getFrom()).items.put(task.getItem(), 0);
                    }
                }
                else {
                    if (graph.getManufacturer().items.get(task.getItem()) > task.getItemNum()){
                        graph.getManufacturer().items.put(task.getItem(), graph.getManufacturer().items.get(task.getItem()) - task.getItemNum());
                        graph.getDistributors().get(task.getTo()).items.put(task.getItem(), graph.getDistributors().get(task.getTo()).items.get(task.getItem()) + task.getItemNum());

                        fitness -= graph.getManufacturer().distributorDistances.get(task.getTo()) * pricePerUnitOfDistance * task.getItemNum();
                        stats.spentOnDelivery +=graph.getDistributors().get(task.getFrom()).retailerDistances.get(task.getTo()) * graph.getDistributors().get(task.getFrom()).items.get(task.getItem());
                    }
                    else if (graph.getManufacturer().items.get(task.getItem()) > 0){
                        graph.getDistributors().get(task.getTo()).items.put(task.getItem(), graph.getDistributors().get(task.getTo()).items.get(task.getItem()) + graph.getManufacturer().items.get(task.getItem()));

                        fitness -= graph.getManufacturer().distributorDistances.get(task.getTo()) * pricePerUnitOfDistance * graph.getManufacturer().items.get(task.getItem());
                        stats.spentOnDelivery += graph.getManufacturer().distributorDistances.get(task.getTo()) * graph.getManufacturer().items.get(task.getItem());

                        graph.getManufacturer().items.put(task.getItem(), 0);
                    }
                }
            }
        }
    }
}
