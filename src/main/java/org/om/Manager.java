package org.om;

import lombok.Getter;
import lombok.Setter;
import org.om.ga.Genotype;
import org.om.ga.Population;
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
            graph.applyGenotype(best, fitnessValues);

            fitnessList.add(best.getFitness());
        }

        System.out.println("Minimum fitness: " + fitnessList.stream().mapToDouble(Double::doubleValue).min().getAsDouble());
        System.out.println("Maximum fitness: " + fitnessList.stream().mapToDouble(Double::doubleValue).max().getAsDouble());
        System.out.println("Average fitness: " + fitnessList.stream().mapToDouble(Double::doubleValue).average().getAsDouble());

        return population.getBestGenotype();
    }


    private ArrayList<HashMap<Person, HashMap<Item, Integer>>> initializePersonChoices(Integer days) {
        ArrayList<HashMap<Person, HashMap<Item, Integer>>> personChoices = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            HashMap<Person, HashMap<Item, Integer>> personChoicesForDay = new HashMap<>(graph.getPersons().size());
            for (Person person : graph.getPersons()) {
                personChoicesForDay.put(person, person.generateNewOrders());
            }
            personChoices.add(personChoicesForDay);
        }
        return personChoices;
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
            // generate new items on manufacture
            graph.getManufacturer().generateNewItems();

            // set predefined choices for each person
            graph.applyPersonChoices(personChoices);

            // apply genotype and calculate fitness
            graph.applyGenotype(genotype, fitnessValues);
        }
    }
}
