package com.cps.signalprocessing.conversion;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

/**
 * Converter used to process signals.
 * @author Szymon Andrzejczak
 */
public class DigitalToAnalogConverter {

    public Signal interpolate(final Signal signal, final double frequency) {
        double startTime = signal.getStartTime();
        double endTime = signal.getEndTime();
        double oldTimeStep = 1.0 / signal.getSamplingFrequency();
        double newTimeStep = 1.0 / frequency;
        int n = (int) Math.ceil((endTime - startTime) * frequency);
        double[] oldValues = signal.getValues();
        double[] newValues = new double[n];
        double time = 0.0;
        for (int i = 0; i < newValues.length; i++) {
            int j = (int) (time / oldTimeStep);
            if (j == oldValues.length - 1) {
                --j;
            }
            double a = (oldValues[j + 1] - oldValues[j]) / oldTimeStep;
            double b = oldValues[j + 1] - a * ((j + 1) * 1.0 * oldTimeStep);
            newValues[i] = a * time + b;
            time += newTimeStep;
        }
        return new RealSignal(newValues, signal.getStartTime(),
            signal.getEndTime(), signal.getAmplitude(), frequency, true);
    }

    public Signal reconstructWithSinc(final Signal signal, final double frequency, int maxProbes) {
        double startTime = signal.getStartTime();
        double endTime = signal.getEndTime();
        double oldTimeStep = 1.0 / signal.getSamplingFrequency();
        double newTimeStep = 1.0 / frequency;
        int n = (int) Math.ceil((endTime - startTime) * frequency);
        double[] oldValues = signal.getValues();
        double[] newValues = new double[n];
        double time = 0.0;
        maxProbes = (int) (Math.min(maxProbes, (endTime - startTime) * signal.getSamplingFrequency()));
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            int k = (int) (Math.max((time / oldTimeStep) - maxProbes, startTime / oldTimeStep));
            k = (int) (Math.max(k, (time / oldTimeStep) - maxProbes / 2));
            k = (int) (Math.min(k, (endTime / oldTimeStep) - maxProbes));
            int maxk = k + maxProbes;
            while (k < maxk) {
                sum += oldValues[k] * sinc(time / oldTimeStep - k);
                k++;
            }
            newValues[i] = sum;
            time += newTimeStep;
        }
        return new RealSignal(newValues, signal.getStartTime(), signal.getEndTime(), signal.getAmplitude(), frequency, false);
    }

    private double sinc(double x) {
        if (Math.abs(x) < 0.0000001) {
            return 1.0;
        } else {
            return (Math.sin(Math.PI * x) / (Math.PI * x));
        }
    }

}
