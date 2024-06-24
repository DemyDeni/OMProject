package org.om.ga.mutation;

import org.om.ga.Task;

import java.util.ArrayList;
import java.util.Random;

public class UniformMutation implements Mutation {
    private final double mutationChance = 0.01;
    @Override
    public ArrayList<Task> mutate(ArrayList<Task> list) {
        Random random = new Random();
        for (int i = 0; i < list.size(); i++){
         if (random.nextDouble(0, 1) < mutationChance){
             if (random.nextBoolean()){
                 Task tempTask = list.get(i);
                 tempTask.setChance(tempTask.getChance()*1.2);
                 tempTask.setItemNum((int) (tempTask.getItemNum()*1.2));
             }
             else {
                 Task tempTask = list.get(i);
                 tempTask.setChance(tempTask.getChance()*0.83333);
                 tempTask.setItemNum((int) (tempTask.getItemNum()*0.83333));
             }
         }
        }
        return list;
    }
}
