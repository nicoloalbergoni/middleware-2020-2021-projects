package com.polimi;

import java.util.Queue;
import java.util.Random;

public class PushThread extends Thread {

    private Queue<Integer> buffer;
    private int maxSize;

    PushThread(Queue<Integer> buff, int mSize) {
        buffer = buff;
        maxSize = mSize;
    }

    @Override
    public void run() {
        synchronized (buffer) {
            super.run();

            if (buffer.size() == maxSize) {
                try {
                    buffer.wait();
                    System.out.println("Waiting to push...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                buffer.add(new Random().nextInt(100));
                System.out.println("Added a value:" + buffer.toString());
                buffer.notify();
            }
        }
    }
}
