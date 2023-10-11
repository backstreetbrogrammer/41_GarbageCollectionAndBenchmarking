package com.backstreetbrogrammer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StaticMemoryLeakDemo {

    public static List<Double> list = new ArrayList<>();

    public void populateList() {
        for (int i = 0; i < 10000000; i++) {
            list.add(ThreadLocalRandom.current().nextDouble());
        }
        System.out.println("Debug Point 2");
    }

    public static void main(final String[] args) {
        System.out.println("Debug Point 1");
        new StaticMemoryLeakDemo().populateList();
        System.out.println("Debug Point 3");
    }
}
