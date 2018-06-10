package com.cps.signalprocessing.filtering;

public class BlackmanWindow implements WindowFunction {

    @Override
    public double getValue(int i, int M) {
        return 0.42 - 0.5 * Math.cos(2 * Math.PI * i / M) + 0.08 * Math.cos(4 * Math.PI * i / M);
    }

}
