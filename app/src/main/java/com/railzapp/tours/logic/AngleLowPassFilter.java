package com.railzapp.tours.logic;

public class AngleLowpassFilter {

    private final int LENGTH = 10;

    private float sumSin, sumCos;

    private ArrayDeque<Float> queue = new ArrayDeque<Float>();

    public void add(float radians){

        sumSin += (float) Math.sin(radians);

        sumCos += (float) Math.cos(radians);

        queue.add(radians);

        if(queue.size() > LENGTH){

            float old = queue.poll();

            sumSin -= Math.sin(old);

            sumCos -= Math.cos(old);
        }
    }

    public float average(){

        int size = queue.size();

        return (float) Math.atan2(sumSin / size, sumCos / size);
    }
}