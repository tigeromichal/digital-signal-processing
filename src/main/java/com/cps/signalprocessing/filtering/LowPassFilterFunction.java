package com.cps.signalprocessing.filtering;

public class LowPassFilterFunction implements FilterFunction {

    @Override
    public double getValue(int i) {
        return 1.0;
    }

    @Override
    public int getK(double samplingFrequency, double frequency) {
        return (int) (samplingFrequency / frequency);
    }
}
