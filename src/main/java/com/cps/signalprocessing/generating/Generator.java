package com.cps.signalprocessing.generating;

import com.cps.signalprocessing.model.Signal;

public interface Generator {

    Signal generate(final double startTime, final double endTime, final double amplitude, final double samplingFrequency);

    //ComplexSignal generateComplex();

}