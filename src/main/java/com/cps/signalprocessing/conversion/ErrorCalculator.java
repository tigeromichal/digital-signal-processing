package com.cps.signalprocessing.conversion;

import com.cps.signalprocessing.model.Signal;

public class ErrorCalculator {

    public double calculateMse(final Signal signal1, final Signal signal2) {
        double[] values1 = signal1.getValues();
        double[] values2 = signal2.getValues();
//        if (values1.length != values2.length || Math.abs(signal1.getSamplingFrequency() - signal2.getSamplingFrequency()) > 0.000001) {
//            throw new IllegalArgumentException("Sampling frequency and lengths of both signals should be same");
//        }
        if (Math.abs(signal1.getSamplingFrequency() - signal2.getSamplingFrequency()) > 0.000001) {
            throw new IllegalArgumentException("Sampling frequency of both signals should be same");
        }
        int len = Math.min(values1.length, values2.length);
        double squaredErrorsSum = 0.0;
        for (int i = 0; i < len; i++) {
            squaredErrorsSum += Math.pow((values1[i] - values2[i]), 2.0);
        }
        return squaredErrorsSum / (len * 1.0);
    }

    public double calculateSnr(final Signal signal1, final Signal signal2) {
        double[] values1 = signal1.getValues();
        double[] values2 = signal2.getValues();
//        if (values1.length != values2.length || Math.abs(signal1.getSamplingFrequency() - signal2.getSamplingFrequency()) > 0.000001) {
//            throw new IllegalArgumentException("Sampling frequency and lengths of both signals should be same");
//        }
        if (Math.abs(signal1.getSamplingFrequency() - signal2.getSamplingFrequency()) > 0.000001) {
            throw new IllegalArgumentException("Sampling frequency of both signals should be same");
        }
        double squaresSum = 0.0;
        double noiseSquaresSum = 0.0;
        int len = Math.min(values1.length, values2.length);
        for (int i = 0; i < len; i++) {
            squaresSum += values1[i] * values1[i];
            noiseSquaresSum += Math.pow(Math.abs(values1[i] - values2[i]), 2.0);
        }
        return 10.0 * Math.log10(squaresSum / noiseSquaresSum);
    }

    public double calculatePsnr(final Signal signal1, final Signal signal2) {
        double[] values1 = signal1.getValues();
        double maxValue = values1[0];
        for (int i = 1; i < values1.length; i++) {
            if (values1[i] > maxValue) {
                maxValue = values1[i];
            }
        }
        return 10.0 * Math.log10(maxValue / calculateMse(signal1, signal2));
    }

    public double calculateMd(final Signal signal1, final Signal signal2) {
        double[] values1 = signal1.getValues();
        double[] values2 = signal2.getValues();
//        if (values1.length != values2.length || Math.abs(signal1.getSamplingFrequency() - signal2.getSamplingFrequency()) > 0.000001) {
//            throw new IllegalArgumentException("Sampling frequency and lengths of both signals should be same");
//        }
        if (Math.abs(signal1.getSamplingFrequency() - signal2.getSamplingFrequency()) > 0.000001) {
            throw new IllegalArgumentException("Sampling frequency of both signals should be same");
        }
        int len = Math.min(values1.length, values2.length);
        double diff = Math.abs(values1[1] - values2[0]);
        for (int i = 1; i < len; i++)
            if (Math.abs(values1[i] - values2[i]) > diff)
                diff = Math.abs(values1[i] - values2[i]);
        return diff;
    }


}
