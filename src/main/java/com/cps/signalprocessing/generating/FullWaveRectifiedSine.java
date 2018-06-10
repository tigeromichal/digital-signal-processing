package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

public class FullWaveRectifiedSine implements Generator {

    private final double period;

    public FullWaveRectifiedSine(final double period) {
        this.period = period;
    }

    @Override
    public Signal generate(double startTime, double endTime, double amplitude, double samplingFrequency) {
        int n = (int) ((endTime - startTime) * samplingFrequency);
        double samplingPeriod = 1.0 / samplingFrequency;
        double time = startTime;
        double[] values = new double[n];
        for (int i = 0; i < n && time <= endTime; i++) {
            values[i] = amplitude * Math.abs(Math.sin(2 * Math.PI / period * (time - startTime)));
            time += samplingPeriod;
        }
        return new RealSignal(values, startTime, endTime, amplitude, samplingFrequency, false);
    }

}
