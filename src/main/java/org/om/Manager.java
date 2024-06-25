package org.om;

import lombok.Getter;
import lombok.Setter;
import org.om.ga.Genotype;
import org.om.ga.Population;
import org.om.ga.Stats;
import org.om.graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class Manager {
    private Graph graph;
    private Population population;
    private FitnessValues fitnessValues;

    public Manager(Integer populationNum, FitnessValues fitnessValues) {
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
                .fitnessValues(fitnessValues)
                .build();
        graph = new Graph(config);
        population = getRandomPopulation(graph, populationNum, fitnessValues);
        this.fitnessValues = fitnessValues;
    }

    public Genotype simulateOneByOne(Integer days, Integer generationsNum) {
        // list of choices for each person for each day
        ArrayList<HashMap<Person, HashMap<Item, Integer>>> personChoices = initializePersonChoices(days);
        List<Double> fitnessList = new ArrayList<>(days);
        Stats stats = new Stats();

        // simulate day-by-day
        for (int d = 0; d < days; d++) {
            for (int g = 0; g < population.getSize(); g++) {
                simulateDayForGenotype(generationsNum, graph.clone(), population.getGenotypes().get(g), personChoices.get(d));
            }

            Population newPopulation = new Population();

            // leave 1/4 of original genotypes
            newPopulation.getGenotypes().addAll(population.getRandomGenotypes(population.getSize() / 4));

            // add 1/4 genotypes after crossover
            newPopulation.getGenotypes().addAll(population.getRandomGenotypesWithCrossover(population.getSize() / 4));

            // add 2/4 genotypes after crossover and mutation
            newPopulation.getGenotypes().addAll(population.getRandomGenotypesWithMutation(population.getSize() / 2));

            // update population
            population = newPopulation;

            // apply best genotype to current graph
            Genotype best = population.getBestGenotype();
            stats = new Stats();
            simulateGenerationForGenotype(graph, best, initializePersonChoicesForGeneration(), stats);

            fitnessList.add(best.getFitness());
        }

        System.out.println("Minimum fitness: " + fitnessList.stream().mapToDouble(Double::doubleValue).min().getAsDouble());
        System.out.println("Maximum fitness: " + fitnessList.stream().mapToDouble(Double::doubleValue).max().getAsDouble());
        System.out.println("Average fitness: " + fitnessList.stream().mapToDouble(Double::doubleValue).average().getAsDouble());
        System.out.println("Sold price: " + stats.getSoldPrice());
        System.out.println("Orders failed: " + stats.getOrdersFailed());
        System.out.println("Storage cost: " + stats.getStorageCost());
        System.out.println("Delivery cost: " + stats.getDeliveryCost());

        return population.getBestGenotype();
    }


    private ArrayList<HashMap<Person, HashMap<Item, Integer>>> initializePersonChoices(Integer days) {
        ArrayList<HashMap<Person, HashMap<Item, Integer>>> personChoices = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            personChoices.add(initializePersonChoicesForGeneration());
        }
        return personChoices;
    }

    private HashMap<Person, HashMap<Item, Integer>> initializePersonChoicesForGeneration() {
        HashMap<Person, HashMap<Item, Integer>> personChoicesForGeneration = new HashMap<>(graph.getPersons().size());
        for (Person person : graph.getPersons()) {
            personChoicesForGeneration.put(person, person.generateNewOrders());
        }
        return personChoicesForGeneration;
    }

    private Population getRandomPopulation(Graph graph, Integer population, FitnessValues fitnessValues) {
        Population newPopulation = new Population();
        for (int i = 0; i < population; i++) {
            newPopulation.getGenotypes().add(Genotype.generateRandomGenotype(graph, fitnessValues));
        }
        return newPopulation;
    }

    private void simulateDayForGenotype(Integer generations, Graph graph, Genotype genotype, HashMap<Person, HashMap<Item, Integer>> personChoices) {
        for (int i = 0; i < generations; i++) {
            simulateGenerationForGenotype(graph, genotype, personChoices, new Stats());
        }
    }

    private void simulateGenerationForGenotype(Graph graph, Genotype genotype, HashMap<Person, HashMap<Item, Integer>> personChoices, Stats stats) {
        // generate new items on manufacture
        graph.getManufacturer().generateNewItems();

        // set predefined choices for each person
        graph.applyPersonChoices(personChoices);

        // apply genotype and calculate fitness
        graph.applyGenotype(genotype, fitnessValues, stats);
    }
}
