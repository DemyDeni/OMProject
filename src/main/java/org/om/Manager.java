package org.om;

import lombok.Getter;
import lombok.Setter;
import org.om.ga.Genotype;
import org.om.ga.Population;
import org.om.graph.Graph;
import org.om.graph.GraphConfig;
import org.om.graph.Item;
import org.om.graph.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class Manager {
    private Graph startingGraph;

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
        startingGraph = new Graph(config);
    }

    public Genotype simulateOneByOne(Integer days, Integer populationNum, Integer generationsNum) {
        // list of choices for each person for each day
        ArrayList<HashMap<Person, HashMap<Item, Integer>>> personChoices = initializePersonChoices(days);

        // graphs for population
        List<Graph> graphs = new ArrayList<>();
        for (int i = 0; i < populationNum; i++) {
            graphs.add(startingGraph.clone());
        }
        // genotypes fer each population
        Population population = getRandomPopulation(startingGraph, populationNum, 10, 0.15d);
        // simulate day-by-day
        for (int d = 0; d < days; d++) {
            for (int g = 0; g < population.getSize(); g++) {
                simulateDayForGenotype(generationsNum, graphs.get(g), population.getGenotypes().get(g), personChoices.get(d));
            }

            Population newPopulation = new Population();
            // leave 1/4 of original genotypes
            newPopulation.getGenotypes().addAll(population.getRandomGenotypes(population.getSize() / 4));

            // add 1/4 genotypes after crossover
            newPopulation.getGenotypes().addAll(population.getRandomGenotypesWithCrossover(population.getSize() / 4));

            // add 2/4 genotypes after crossover and mutation
            newPopulation.getGenotypes().addAll(population.getRandomGenotypesWithMutation(population.getSize() / 4));

            population = newPopulation;
        }

        return null;
    }


    private ArrayList<HashMap<Person, HashMap<Item, Integer>>> initializePersonChoices(Integer days) {
        ArrayList<HashMap<Person, HashMap<Item, Integer>>> personChoices = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            HashMap<Person, HashMap<Item, Integer>> personChoicesForDay = new HashMap<>(startingGraph.getPersons().size());
            for (Person person : startingGraph.getPersons()) {
                personChoicesForDay.put(person, person.generateNewOrders(startingGraph.getItems()));
            }
            personChoices.add(personChoicesForDay);
        }
        return personChoices;
    }

    private Population getRandomPopulation(Graph graph, Integer population, Integer moveItemNum, Double moveItemChance) {
        Population newPopulation = new Population();
        for (int i = 0; i < population; i++) {
            newPopulation.getGenotypes().add(Genotype.generateRandomGenotype(graph, 10, 0.15d));
        }
        return newPopulation;
    }

    private void simulateDayForGenotype(Integer generations, Graph graph, Genotype genotype, HashMap<Person, HashMap<Item, Integer>> personChoices) {
        for (int i = 0; i < generations; i++) {
            // generate new items on manufacture
            graph.getManufacturer().generateNewItems();

            // set predefined choices for each person
            graph.applyPersonChoices(personChoices);

            // apply genotype and calculate fitness
            graph.applyGenotype(genotype);
        }
    }
}
