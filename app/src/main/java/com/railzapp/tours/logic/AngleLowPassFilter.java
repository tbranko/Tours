package com.railzapp.tours.logic;

import java.util.ArrayDeque;

public class AngleLowPassFilter {

    private final int LENGTH = 10;

    private float sumSin, sumCos;

    private ArrayDeque<Double> queue = new ArrayDeque<Double>();

    public void add(double degrees) {
        double radians = Math.toRadians(degrees);

        sumSin += (double) Math.sin(radians);

        sumCos += (double) Math.cos(radians);

        queue.add(radians);

        if (queue.size() > LENGTH) {

            double old = queue.poll();

            sumSin -= Math.sin(old);

            sumCos -= Math.cos(old);
        }
    }

    public float average() {

        int size = queue.size();
        double result = Math.atan2(sumSin / size, sumCos / size);
        return (float)Math.toDegrees(result);
    }

    public boolean isReady() {
        if (queue.size() >= LENGTH) return true;
        return false;
    }
}