package com.polimi;

import java.util.Queue;

public class PullThread extends Thread {


    private Queue<Integer> buffer;
    private int maxSize;

    PullThread(Queue<Integer> buff, int mSize) {
        buffer = buff;
        maxSize = mSize;
    }

    @Override
    public void run() {
        synchronized (buffer) {
            super.run();

            if (buffer.size() == 0) {
                try {
                    buffer.wait();
                    System.out.println("Waiting to remove...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                buffer.poll();
                System.out.println("Removed a value:" + buffer.toString());
                buffer.notify();
            }
        }
    }
}
