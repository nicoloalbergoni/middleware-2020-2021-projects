package com.polimi;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int length = 3;
        int[][] matrixA = {{6, 9, 9}, {1, 2, 7}, {12, 4, 6}};
        int[][] matrixB = {{3, 5, 2}, {0, 6, 9}, {1, 1, 2}};
        int[][] result = new int[length][length];
        ArrayList<MatrixThread> tList = new ArrayList<MatrixThread>();


        MatrixThread matrixThread;
        for (int i = 0; i < length; i++) {
            matrixThread = new MatrixThread(matrixA[i], matrixB[i], result, i);
            matrixThread.start();
            tList.add(matrixThread);
        }

        tList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }
}
