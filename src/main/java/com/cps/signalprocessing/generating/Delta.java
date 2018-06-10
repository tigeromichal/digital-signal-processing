package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

public class Delta implements Generator {

    private final int stepSampleNumber;

    public Delta(int stepSampleNumber) {
        this.stepSampleNumber = stepSampleNumber;
    }

    @Override
    public Signal generate(double startTime, double endTime, double amplitude, double samplingFrequency) {
        int n = (int) ((endTime - startTime) * samplingFrequency);
        double samplingPeriod = 1.0 / samplingFrequency;
        double time = startTime;
        double[] values = new double[n];
        for (int i = 0; i < n && time <= endTime; i++) {
            if (i == stepSampleNumber) {
                values[i] = amplitude;
            } else {
                values[i] = 0;
            }

            time += samplingPeriod;
        }
        return new RealSignal(values, startTime, endTime, amplitude, samplingFrequency, true);
    }

}
