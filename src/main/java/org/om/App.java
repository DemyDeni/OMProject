package org.om;

import org.om.graph.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) {
//        Manager manager = new Manager();

        Item item1 = new Item(1, 10, 0.5);
        Item item2 = item1.clone();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String name = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
