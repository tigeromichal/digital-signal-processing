package com.cps.signalprocessing.filtering;

public interface FilterFunction {

    double getValue(final int i);

    int getK(final double samplingFrequency, final double frequency);

}
