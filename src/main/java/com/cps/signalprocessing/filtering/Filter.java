package com.cps.signalprocessing.filtering;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    private WindowFunction windowFunction;
    private FilterFunction filterFunction;
    private int M;
    private double frequency;

    public Filter(WindowFunction windowFunction, FilterFunction filterFunction, int M, double frequency) {
        this.windowFunction = windowFunction;
        this.filterFunction = filterFunction;
        this.M = M;
        this.frequency = frequency;
    }

    public List<Signal> apply(Signal signal) {
        final int K = filterFunction.getK(signal.getSamplingFrequency(), frequency);
        double[] newValues = new double[M];
        for (int i = 0; i < M; i++) {
            if (i == (M - 1) / 2) {
                newValues[i] = 2.0 / K;
            } else {
                newValues[i] = Math.sin((2.0 * Math.PI * (i - (M - 1) / 2)) / K) / (Math.PI * (i - (M - 1) / 2));
            }
            newValues[i] *= windowFunction.getValue(i, M);
            newValues[i] *= filterFunction.getValue(i - (M - 1) / 2);
        }
        Signal h = new RealSignal(newValues, signal.getStartTime(), 1.0 / signal.getSamplingFrequency() * M, signal.getAmplitude(), signal.getSamplingFrequency(), true);
        ArrayList<Signal> res = new ArrayList<>();
        res.add(h);
        Signal convolution = h.convolution(signal);
        double[] convolutionValues = convolution.getValues();
        double[] newConvolutionValues = new double[convolutionValues.length - M + 1];
        for (int i = 0; i < newConvolutionValues.length; i++) {
            newConvolutionValues[i] = convolutionValues[i + (M - 1) / 2];
        }
        res.add(new RealSignal(newConvolutionValues, convolution.getStartTime(),
                convolution.getEndTime() - (M - 1) / convolution.getSamplingFrequency(),
                convolution.getAmplitude(), convolution.getSamplingFrequency(), false));
        return res;
    }

}
