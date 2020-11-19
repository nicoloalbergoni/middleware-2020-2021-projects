package com.polimi;

public class CountThread extends Thread {

    private int[] intArr;
    private  int pos;
    private int[] countResult;

    CountThread(int[] intA, int[] c, int p) {
        this.intArr = intA;
        this.countResult = c;
        this.pos = p;
    }

    @Override
    public void run() {
        super.run();

        int index = pos * (Main.N/Main.numberOfThreads);
        int count = 0;
        for (int i = index; i < index + (Main.N / Main.numberOfThreads); i++) {
            if (intArr[i] % 3 == 0) count+= 1;
        }

        System.out.println(this.getName() + " Position:" + pos + " Count:" + count);
        countResult[pos] = count;
    }
}
