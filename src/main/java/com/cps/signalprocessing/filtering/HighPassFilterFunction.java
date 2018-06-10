package com.cps.signalprocessing.filtering;

public class HighPassFilterFunction implements FilterFunction {

    @Override
    public double getValue(int i) {
        return Math.pow(-1, i);
    }

    @Override
    public int getK(double samplingFrequency, double frequency) {
        //return (int) (samplingFrequency / frequency);
        return (int) (samplingFrequency / (samplingFrequency / 2.0 - frequency));
    }

}
