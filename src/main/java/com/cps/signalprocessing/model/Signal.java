package com.cps.signalprocessing.model;

public abstract class Signal {

    protected final double[] values;
    protected final double startTime;
    protected final double endTime;
    protected final double samplingFrequency;
    protected boolean isDiscrete;
    protected double amplitude;

    public Signal(double[] values, double startTime, double endTime, double amplitude, double samplingFrequency, boolean isDiscrete) {
        this.values = values;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amplitude = amplitude;
        this.samplingFrequency = samplingFrequency;
        this.isDiscrete = isDiscrete;
    }

    public Signal(double[] values, double startTime, double endTime, double amplitude, double samplingFrequency) {
        this(values, startTime, endTime, amplitude, samplingFrequency, true);
    }

    public Signal(double[] values, double startTime, double samplingFrequency) {
        this.values = values;
        this.startTime = startTime;
        this.samplingFrequency = samplingFrequency;
        amplitude = Math.abs(values[0]);
        for (int i = 1; i < values.length; i++) {
            if (Math.abs(values[i]) > amplitude) {
                amplitude = Math.abs(values[i]);
            }
        }
        endTime = startTime + values.length * 1.0 / samplingFrequency;
        this.isDiscrete = true;
    }

    public abstract Signal add(final Signal signal);

    public abstract Signal subtract(final Signal signal);

    public abstract Signal multiply(final Signal signal);

    public abstract Signal divide(final Signal signal);

    public abstract Signal negate();

    public abstract Signal inverse();

    public abstract Signal reverse();

    public abstract Signal convolution(final Signal signal);

    public abstract Signal correlation(final Signal signal);

    public abstract Signal correlationUsingConvolution(final Signal signal);

    public abstract double average();

    public abstract double averageMagnitude();

    public abstract double power();

    public abstract double variance();

    public abstract double effectiveValue();

    public abstract String getType();

    public double[] getValues() {
        return values;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getSamplingFrequency() {
        return samplingFrequency;
    }

    public boolean isDiscrete() {
        return isDiscrete;
    }

    public void setDiscrete(boolean isDiscrete) {
        this.isDiscrete = isDiscrete;
    }

}
