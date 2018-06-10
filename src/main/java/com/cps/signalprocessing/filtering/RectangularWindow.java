package com.cps.signalprocessing.filtering;

public class RectangularWindow implements WindowFunction {

    @Override
    public double getValue(int i, int M) {
        return 1.0;
    }

}
