package com.cps.signalprocessing.model;

public class ComplexSignal extends Signal {

    protected final double[] imaginaryValues;

    public ComplexSignal(double[] values, double[] imaginaryValues, double startTime, double endTime, double amplitude, double samplingFrequency, boolean isDiscrete) {
        super(values, startTime, endTime, amplitude, samplingFrequency, isDiscrete);
        this.imaginaryValues = imaginaryValues;
    }

    public ComplexSignal(double[] values, double[] imaginaryValues, double startTime, double endTime, double amplitude, double samplingFrequency) {
        super(values, startTime, endTime, amplitude, samplingFrequency, true);
        this.imaginaryValues = imaginaryValues;
    }

    public ComplexSignal(double[] values, double[] imaginaryValues, double startTime, double samplingFrequency) {
        super(values, startTime, samplingFrequency);
        this.imaginaryValues = imaginaryValues;
    }

    @Override
    public Signal add(Signal signal) {
        return null;
    }

    @Override
    public Signal subtract(Signal signal) {
        return null;
    }

    @Override
    public Signal multiply(Signal signal) {
        return null;
    }

    @Override
    public Signal divide(Signal signal) {
        return null;
    }

    @Override
    public Signal negate() {
        return null;
    }

    @Override
    public Signal inverse() {
        return null;
    }

    @Override
    public Signal reverse() {
        return null;
    }

    @Override
    public Signal convolution(Signal signal) {
        return null;
    }

    @Override
    public Signal correlation(Signal signal) {
        return null;
    }

    @Override
    public Signal correlationUsingConvolution(Signal signal) {
        return null;
    }

    @Override
    public double average() {
        return 0;
    }

    @Override
    public double averageMagnitude() {
        return 0;
    }

    @Override
    public double power() {
        return 0;
    }

    @Override
    public double variance() {
        return 0;
    }

    @Override
    public double effectiveValue() {
        return 0;
    }

    @Override
    public String getType() {
        return "COMPLEX";
    }

    public double[] getImaginaryValues() {
        return imaginaryValues;
    }

}
