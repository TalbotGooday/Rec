package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;


public class LowPassFilter {
    private int N = 7;
    private double FCP = 0.1d;

    public LowPassFilter(int n, double FCP) {
        N = n;
        this.FCP = FCP;
    }

    public List<Float> applyLPFilter(List<Float> band) {
        List<Float> filteredSpectrum = new ArrayList<>();
        float y;

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
            filteredSpectrum.add(y);
        }
        return filteredSpectrum;
    }

    private double c(int k) {
        double PI = Math.PI;
        return k == 0 ? 2 * FCP : ((1 / (k * PI)) * Math.sin(2 * k * PI * FCP));
    }
}
