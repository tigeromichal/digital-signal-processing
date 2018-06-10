package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

import java.util.Random;

public class GaussianNoise implements Generator {

    private final Random random = new Random();
    private final double average;
    private final double standardDeviation;

    public GaussianNoise(double average, double standardDeviation) {
        this.average = average;
        this.standardDeviation = standardDeviation;
    }

    @Override
    public Signal generate(double startTime, double endTime, double amplitude, double samplingFrequency) {
        int n = (int) ((endTime - startTime) * samplingFrequency);
        double samplingPeriod = 1.0 / samplingFrequency;
        double time = startTime;
        double[] values = new double[n];
        for (int i = 0; i < n && time <= endTime; i++) {
            values[i] = random.nextGaussian() * standardDeviation + average;
            time += samplingPeriod;
        }
        return new RealSignal(values, startTime, endTime, amplitude, samplingFrequency, false);
    }

}
