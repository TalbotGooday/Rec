package com.example.talbotgooday.rec.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chebyshev {
    private int N = 256;
    private int cSize = 256;
    private float PI = (float)Math.PI;


    private float cm(List<Float> s, int k, int step) {
        double c = 0;
        for (int i = step * N; i < (step + 1) * N; i++) {
            c += s.get(i) * Math.cos((PI * k * (i + 0.5f)) / N);
        }
        return (float)(Math.sqrt(2.f / N) * c);
    }

    private float s_m(List<Float> s, int k, int step) {
        float sm = 0;
        for (int i = 0; i < k; i++) {
            sm += g(k) * cm(s, k, step) * Math.cos(i * Math.acos(Math.cos(PI * (i + 0.5) / N)));
        }
        return sm;
    }

    private float g(int m) {
        return m == 0 ? (float)Math.sqrt(0.5f) : 1;
    }

    private float a(List<Float> s, int k, int step) {
        float a = 0;
        float min = Collections.min(s);
        float max = Collections.max(s);

        for (int i = step * N; i < (step + 1) * N; i++) {
            float ki = ((2 * s.get(i)) - (max + min)) / (max - min);
            float t = (float)Math.cos(k * Math.acos(ki));
            a += s.get(i) * Math.cos((2 * PI * k * i) / N);
        }
        return 2 * a / N;
    }

    private float b(List<Float> s, int k, int step) {
        float b = 0;

        float min = Collections.min(s);
        float max = Collections.max(s);
        for (int i = step * N; i < (step + 1) * N; i++) {
            float ki = ((2 * s.get(i)) - (max + min)) / (max - min);
            float t = (float)Math.cos(k * Math.acos(ki));
            b += s.get(i) * Math.sin((2 * PI * k * i) / N);
        }
        return 2 * b / N;
    }

    private float t(List<Float> s, int k, int step) {

        double c = cm(s, k, step);
        return (float)Math.sqrt(c * c);
    }

    public List<List<Float>> chebyshev(WavModel wavModel) {

        List<Float> s = wavModel.getNormalizedList();

        int spectrCount = s.size() / N;
        List<List<Float>> ck = new ArrayList<>();
        for (int i = 0; i < spectrCount; i++) {
            ck.add(new ArrayList<Float>());
            for (int j = 0; j < cSize; j++)
                ck.get(i).add(t(s, j, i));
        }
        return ck;
    }
}
