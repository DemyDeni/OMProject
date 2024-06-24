package org.om;

import lombok.Getter;
import lombok.Setter;
import org.om.graph.Graph;
import org.om.graph.GraphConfig;
import org.om.graph.Item;
import org.om.graph.Person;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class Manager {
    private Graph graph;

    // list of choices for each person for each day
    private ArrayList<HashMap<Person, HashMap<Item, Integer>>> personChoices;

    public Manager() {
        GraphConfig config = GraphConfig.builder()
                .itemsNum(5)
                .itemMinPrice(10)
                .itemMaxPrice(30)
                .itemMinMultiplier(0.5)
                .itemMaxMultiplier(1.5)
                .personsNum(10)
                .personsMinOrders(1)
                .personsMaxOrders(10)
                .retailersNum(5)
                .distributorsNum(2)
                .manufacturerItemsPerDay(60)
                .build();
        graph = new Graph(config);
    }

    public void initializePersonChoices(int days) {
        personChoices = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            HashMap<Person, HashMap<Item, Integer>> personChoicesForDay = new HashMap<>(graph.getPersons().size());
            for (Person person : graph.getPersons()) {
                personChoicesForDay.put(person, person.generateNewOrders(graph.getItems()));
            }
            personChoices.add(personChoicesForDay);
        }
    }


    public void nextDay() {
//        manufacturer.generateNewItems();
//        persons.forEach(p -> p.generateNewOrders(items));

//        List<Task> tasks = ga.getTasks();
//        updateGraph(tasks);
    }

    public void simulate(int days) {

    }
}
