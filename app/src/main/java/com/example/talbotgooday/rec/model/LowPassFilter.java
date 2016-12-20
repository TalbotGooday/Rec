package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;


public class LowPassFilter {
    private int N = 7;
    private double FCP = 0.1d;
    private double PI = Math.PI;

    public LowPassFilter(int n, double FCP) {
        N = n;
        this.FCP = FCP;
    }

    public List<Float> applyLPfilter(List<Float> band) {
        List<Float> filtredSpectr = new ArrayList<>();
        float y = 0;
        for (int l = 0; l < band.size(); l++) {
            y = 0;
            for (int k = -N; k <= N; k++) {
                if (l - k < 0) {
                    y += c(k) * band.get(0);
                } else if (l - k > band.size() - 1) {
                    y += c(k) * band.get(band.size() - 1);
                } else
                    y += c(k) * band.get(l - k);
            }
            filtredSpectr.add(y);
        }
        return filtredSpectr;
    }

    public List<Float> applyLPfilter2(List<Float> band) {
        int N1 = 128 * 2;
        List<Float> filtredSpectr = new ArrayList<>();
        double y = 0;
        for (int l = 0; l + N1 < band.size(); l += N1) {
            y = 0;

            for (int k = l; k < l + N1 - 1; k++) {
                y = Math.max(y, Math.abs(band.get(k)));
            }

            for (int i = 0; i < N1; i++) {
                filtredSpectr.add((float) y);
            }

        }
        return filtredSpectr;
    }

    private double c(int k) {
        return k == 0 ? 2 * FCP : ((1 / (k * PI)) * Math.sin(2 * k * PI * FCP));
    }
}
