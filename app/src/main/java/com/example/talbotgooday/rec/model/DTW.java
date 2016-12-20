package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;

public class DTW {
    private List<List<Double>> seq1;
    private List<List<Double>> seq2;
    private int[][] warpingPath;

    private int n;
    private int m;
    private int K;
    private int vectorSize;
    private double warpingDistance;

    public DTW(List<List<Double>> sample, List<List<Double>> templete) {
        seq1 = sample;
        seq2 = templete;

        n = seq1.get(0).size();
        m = seq2.get(0).size();
        vectorSize = seq1.size();
        K = 1;

        warpingPath = new int[n + m][2];    // max(n, m) <= K < n + m
        warpingDistance = 0.0;

        this.compute();
    }

    public void compute() {
        double accumulatedDistance = 0.0;
        List<List<Double>> d = new ArrayList<>();
        //double[][] d = new double[n][m];	// local distances
        double[][] D = new double[n][m];    // global distances

        for (int i = 0; i < n; i++) {
            d.add(new ArrayList<Double>());
            for (int j = 0; j < m; j++) {
                double dist = 0;
                for (int k = 0; k < vectorSize; k++) {
                    dist += Math.pow(seq1.get(k).get(i) - seq2.get(k).get(j), 2);
                }
                d.get(d.size() - 1).add(Math.sqrt(dist));
            }
        }

        D[0][0] = d.get(0).get(0);

        for (int i = 1; i < n; i++) {
            D[i][0] = d.get(i).get(0) + D[i - 1][0];
        }

        for (int j = 1; j < m; j++) {
            D[0][j] = d.get(0).get(j) + D[0][j - 1];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m; j++) {
                accumulatedDistance = Math.min(Math.min(D[i - 1][j], D[i - 1][j - 1]), D[i][j - 1]);
                accumulatedDistance += d.get(i).get(j);//[i][j];
                D[i][j] = accumulatedDistance;
            }
        }
        accumulatedDistance = D[n - 1][m - 1];

        int i = n - 1;
        int j = m - 1;
        int minIndex = 1;

        warpingPath[K - 1][0] = i;
        warpingPath[K - 1][1] = j;

        while ((i + j) != 0) {
            if (i == 0) {
                j -= 1;
            } else if (j == 0) {
                i -= 1;
            } else {    // i != 0 && j != 0
                double[] array = {D[i - 1][j], D[i][j - 1], D[i - 1][j - 1]};
                minIndex = this.getIndexOfMinimum(array);

                if (minIndex == 0) {
                    i -= 1;
                } else if (minIndex == 1) {
                    j -= 1;
                } else if (minIndex == 2) {
                    i -= 1;
                    j -= 1;
                }
            } // end else
            K++;
            warpingPath[K - 1][0] = i;
            warpingPath[K - 1][1] = j;
        } // end while
        warpingDistance = accumulatedDistance / K;

        this.reversePath(warpingPath);
    }

    private void reversePath(int[][] path) {
        int[][] newPath = new int[K][2];
        for (int i = 0; i < K; i++) {
            System.arraycopy(path[K - i - 1], 0, newPath[i], 0, 2);
        }
        warpingPath = newPath;
    }

    public double getDistance() {
        return warpingDistance;
    }

    private int getIndexOfMinimum(double[] array) {
        int index = 0;
        double val = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] < val) {
                val = array[i];
                index = i;
            }
        }
        return index;
    }

    public String toString() {
        String retVal = "Warping Distance: " + warpingDistance + "\n";
        retVal += "Warping Path: {";
        for (int i = 0; i < K; i++) {
            retVal += "(" + warpingPath[i][0] + ", " + warpingPath[i][1] + ")";
            retVal += (i == K - 1) ? "}" : ", ";

        }
        return retVal;
    }

}
