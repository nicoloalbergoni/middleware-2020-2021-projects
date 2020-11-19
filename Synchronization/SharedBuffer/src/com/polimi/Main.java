package com.polimi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Queue<Integer> buffer = new LinkedList<Integer>();
        System.out.println(buffer.size());
        int maxBuff = 5;
        ArrayList<Thread> tList = new ArrayList<Thread>();

        Thread t;
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            if (r.nextBoolean()) t = new PushThread(buffer, maxBuff);
            else t = new PullThread(buffer, maxBuff);
            t.start();
            tList.add(t);
        }

        tList.forEach(e-> {
            try {
                e.join();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }
}
