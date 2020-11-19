package com.polimi;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    final static int N = 20;
    final static int numberOfThreads = 4;

    public static void main(String[] args) {


        int[] intArr = new int[N];
        int[] result;
        int[] countResult = new int[numberOfThreads];


        Arrays.setAll(intArr, i -> i + 1);
        System.out.println(Arrays.toString(intArr));

        ArrayList<CountThread> tCount = new ArrayList<CountThread>();

        for (int i = 0; i < numberOfThreads; i++) {
            CountThread t = new CountThread(intArr, countResult, i);
            tCount.add(t);
            t.start();
        }

        tCount.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        System.out.println("Count result:" + Arrays.toString(countResult));

        int resultSize = Arrays.stream(countResult).sum();
        System.out.println("Total size of result is: " + resultSize);
        result = new int[resultSize];

    }
}
