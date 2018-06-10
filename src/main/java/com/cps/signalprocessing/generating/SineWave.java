package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

public class SineWave implements Generator {

    private final double period;

    public SineWave(final double period) {
        this.period = period;
    }

    @Override
    public Signal generate(final double startTime, final double endTime, final double amplitude, final double samplingFrequency) {
        int n = (int) ((endTime - startTime) * samplingFrequency);
        double samplingPeriod = 1.0 / samplingFrequency;
        double time = startTime;
        double[] values = new double[n];
        for (int i = 0; i < n && time <= endTime; i++) {
            values[i] = amplitude * Math.sin(Math.PI * 2 / period * (time - startTime));
            time += samplingPeriod;
        }
        return new RealSignal(values, startTime, endTime, amplitude, samplingFrequency, false);
    }

    public double getValue(final double startTime, final double time, final double amplitude) {
        return amplitude * Math.sin(Math.PI * 2 / period * (time - startTime));
    }

}
