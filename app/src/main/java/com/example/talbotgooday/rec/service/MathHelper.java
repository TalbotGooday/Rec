package com.example.talbotgooday.rec.service;

import java.util.List;

public class MathHelper {

    public static float dispersion(List<Float> data) {
        long d = 0;
        for (double bt : data) {
            d += (bt * bt) / data.size();
        }
        return (float)Math.sqrt(d);
    }

    public static float threshold(List<Float> data) {
        return (1f / 3f) * dispersion(data);
    }

}
