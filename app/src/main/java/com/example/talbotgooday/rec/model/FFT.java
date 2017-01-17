package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.List;

public class FFT {
    private final int N = 256;
    private final float PI = (float)Math.PI;

    private float a(List<Float> s, int k, int step) {
        float a = 0;

        for (int i = step * N; i < (step + 1) * N; i++) {
            a += s.get(i) * Math.cos((2 * PI * k * i) / N);
        }
        return 2 * a / N;
    }

    private float b(List<Float> s, int k, int step) {
        float b = 0;
        int j = 1;
        for (int i = step * N; i < (step + 1) * N; i++) {
            b += s.get(i) * Math.sin((2 * PI * k * i) / N);
        }
        return 2 * b / N;
    }

    private float c(List<Float> s, int k, int step) {
        float a = a(s, k, step);
        float b = b(s, k, step);
        return (float)Math.sqrt(a * a + b * b);
    }

    public List<List<Float>> fff(WavModel wavModel) {
        List<Float> s = wavModel.getNormalizedList();
        int spectrumCount = s.size() / N;
        List<List<Float>> ck = new ArrayList<>();
        for (int i = 0; i < spectrumCount; i++) {
            ck.add(new ArrayList<Float>());
            int cSize = 128;
            for (int j = 0; j < cSize; j++)
                ck.get(i).add(c(s, j, i));
        }
        return ck;
    }
}
