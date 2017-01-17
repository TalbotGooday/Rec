package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;

public class DTW {
    private List<List<Float>> mSample;
    private List<List<Float>> mTemplate;
    private int[][] mWarpingPath;

    private int mMatrixN;
    private int mMatrixM;
    private int mStepsCount;
    private int mVectorSize;
    private double mVarpingDistance;

    public DTW(List<List<Float>> sample, List<List<Float>> template) {
        this.mSample = sample;
        this.mTemplate = template;

        mMatrixN = mSample.get(0).size();
        mMatrixM = mTemplate.get(0).size();
        mVectorSize = mSample.size();
        mStepsCount = 1;

        mWarpingPath = new int[mMatrixN + mMatrixM][2];    // max(mMatrixN, mMatrixM) <= mStepsCount < mMatrixN + mMatrixM
        mVarpingDistance = 0.0;

        this.compute();
    }

    private void compute() {
        double accumulatedDistance;
        List<List<Double>> d = new ArrayList<>();
        //double[][] d = new double[mMatrixN][mMatrixM];	// local distances
        double[][] D = new double[mMatrixN][mMatrixM];    // global distances

        for (int i = 0; i < mMatrixN; i++) {
            d.add(new ArrayList<Double>());
            for (int j = 0; j < mMatrixM; j++) {
                double dist = 0;
                for (int k = 0; k < mVectorSize; k++) {
                    dist += Math.pow((double) mSample.get(k).get(i) - (double) mTemplate.get(k).get(j), 2);
                }

                d.get(d.size() - 1).add(Math.sqrt(dist));
            }
        }

        D[0][0] = d.get(0).get(0);

        for (int i = 1; i < mMatrixN; i++) {
            D[i][0] = d.get(i).get(0) + D[i - 1][0];
        }

        for (int j = 1; j < mMatrixM; j++) {
            D[0][j] = d.get(0).get(j) + D[0][j - 1];
        }

        for (int i = 1; i < mMatrixN; i++) {
            for (int j = 1; j < mMatrixM; j++) {
                accumulatedDistance = Math.min(Math.min(D[i - 1][j], D[i - 1][j - 1]), D[i][j - 1]);
                accumulatedDistance += d.get(i - 1).get(j - 1);//[i][j];
                D[i][j] = accumulatedDistance;
            }
        }

        accumulatedDistance = D[mMatrixN - 1][mMatrixM - 1];

        int i = mMatrixN - 1;
        int j = mMatrixM - 1;
        int minIndex;

        mWarpingPath[mStepsCount - 1][0] = i;
        mWarpingPath[mStepsCount - 1][1] = j;

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
            mStepsCount++;
            mWarpingPath[mStepsCount - 1][0] = i;
            mWarpingPath[mStepsCount - 1][1] = j;
        } // end while

        mVarpingDistance = accumulatedDistance / mStepsCount;

        mWarpingPath = this.reversePath(mWarpingPath);
    }

    private int[][] reversePath(int[][] path) {
        int[][] newPath = new int[mStepsCount][2];
        for (int i = 0; i < mStepsCount; i++) {
            System.arraycopy(path[mStepsCount - i - 1], 0, newPath[i], 0, 2);
        }

        return newPath;
    }

    public double getDistance() {
        return mVarpingDistance;
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
}