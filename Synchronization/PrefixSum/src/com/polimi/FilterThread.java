package com.polimi;

public class FilterThread extends Thread {

    private int[] buffer;
    private int[] result;

    FilterThread(int[] buf, int[] res) {
        buffer = buf;
        result = res;
    }

    @Override
    public void run() {
        super.run();



    }
}
