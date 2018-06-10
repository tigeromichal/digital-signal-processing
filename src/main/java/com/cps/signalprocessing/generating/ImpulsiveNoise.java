package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

import java.util.Random;

public class ImpulsiveNoise implements Generator {

    private final Random random = new Random();
    private final double p;

    public ImpulsiveNoise(final double p) {
        this.p = p;
    }

    @Override
    public Signal generate(double startTime, double endTime, double amplitude, double samplingFrequency) {
        int n = (int) ((endTime - startTime) * samplingFrequency);
        double samplingPeriod = 1.0 / samplingFrequency;
        double time = startTime;
        double[] values = new double[n];
        for (int i = 0; i < n && time <= endTime; i++) {
            if (random.nextDouble() <= p) {
                values[i] = amplitude;
            } else {
                values[i] = 0;
            }
            time += samplingPeriod;
        }
        return new RealSignal(values, startTime, endTime, amplitude, samplingFrequency, true);
    }

}
