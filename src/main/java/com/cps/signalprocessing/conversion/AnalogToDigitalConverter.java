package com.cps.signalprocessing.conversion;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

public class AnalogToDigitalConverter {

    public Signal sample(final Signal signal, final double probingFrequency) {
        double[] values = signal.getValues();
        double samplingFrequency = signal.getSamplingFrequency();
        if (samplingFrequency % probingFrequency > 0.000001) {
            throw new IllegalArgumentException("Sampling frequency of base signal should be multiplication of probing frequency");
        }
        int k = (int) (samplingFrequency / probingFrequency);
        double[] newValues = new double[(int) ((values.length * 1.0) * probingFrequency / samplingFrequency) + 1];
        for (int i = 0; i < newValues.length - 1; i++) {
            newValues[i] = values[i * k];
        }
        newValues[newValues.length - 1] = values[values.length - 1];
        return new RealSignal(newValues, signal.getStartTime(), signal.getEndTime(), signal.getAmplitude(), probingFrequency, true);
    }

    public Signal quantize(final Signal signal, final int bits) {
        double[] values = signal.getValues();
        int levels = (int) (Math.pow(2, bits)) - 1;
        double minValue = values[0];
        for (double value : values) {
            if (value < minValue) {
                minValue = value;
            }
        }
        double maxValue = values[0];
        for (double value : values) {
            if (value > maxValue) {
                maxValue = value;
            }
        }
        double span = (maxValue - minValue) / levels;
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = minValue + Math.round((values[i] - minValue) / span) * span;
        }
        return new RealSignal(newValues, signal.getStartTime(), signal.getEndTime(), signal.getAmplitude(), signal.getSamplingFrequency(), true);
    }

}
