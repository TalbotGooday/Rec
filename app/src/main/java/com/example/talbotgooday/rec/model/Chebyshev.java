package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;

public class Chebyshev {
    private final int N = 256;

    private float funcCM(List<Float> s, int k, int step) {
        double c = 0;
        for (int i = step * N; i < (step + 1) * N; i++) {
            float PI = (float) Math.PI;
            c += s.get(i) * Math.cos((PI * k * (i + 0.5f)) / N);
        }
        return (float) (Math.sqrt(2.f / N) * c);
    }

    private float funcT(List<Float> s, int k, int step) {

        double c = funcCM(s, k, step);
        return (float) Math.sqrt(c * c);
    }

    public List<List<Float>> getChebyshevResult(WavModel wavModel) {
        List<Float> s = wavModel.getNormalizedList();

        int spectrumCount = s.size() / N;
        List<List<Float>> ck = new ArrayList<>();
        for (int i = 0; i < spectrumCount; i++) {
            ck.add(new ArrayList<Float>());
            int size = 256;
            for (int j = 0; j < size; j++)
                ck.get(i).add(funcT(s, j, i));
        }
        return ck;
    }
}
