package com.polimi;

public class MatrixThread extends Thread {

    private int[] rowA;
    private int[] rowB;
    private int[][] result;
    private int row;

    MatrixThread(int[] rA, int[] rB, int[][] res, int i) {
        rowA = rA;
        rowB = rB;
        result = res;
        row = i;
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < rowA.length; i++) {
            result[row][i] = rowA[i] + rowB[i];
        }
    }
}
