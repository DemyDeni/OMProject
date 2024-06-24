package org.om.ga.mutation;

import org.om.ga.Task;

import java.util.ArrayList;

public interface Mutation {
    ArrayList<Task> mutate(ArrayList<Task> list);
}
