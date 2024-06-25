package org.om;

import org.om.ga.Genotype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Genotype best = manager.simulateOneByOne(10, 100, 100);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
