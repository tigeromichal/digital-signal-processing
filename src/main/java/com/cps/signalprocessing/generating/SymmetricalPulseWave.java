package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

public class SymmetricalPulseWave implements Generator {

    private final double dutyCycle;
    private final double period;

    public SymmetricalPulseWave(double dutyCycle, double period) {
        this.dutyCycle = dutyCycle;
        this.period = period;
    }

    @Override
    public Signal generate(final double startTime, final double endTime, final double amplitude, final double samplingFrequency) {
        int n = (int) ((endTime - startTime) * samplingFrequency);
        double samplingPeriod = 1.0 / samplingFrequency;
        double time = startTime;
        double[] values = new double[n];
        for (int i = 0; i < n && time <= endTime; i++) {
            double offset = (int) (time / period) * 1.0 * period;
            if (time - offset >= 0 && time - offset <= dutyCycle * period) {
                values[i] = amplitude;
            } else if (time - offset >= dutyCycle * period && time - offset <= period) {
                values[i] = -amplitude;
            }
            time += samplingPeriod;
        }
        return new RealSignal(values, startTime, endTime, amplitude, samplingFrequency, false);
    }

}
