package com.cps.signalprocessing.model;

public class RealSignal extends Signal {

    public RealSignal(double[] values, double startTime, double endTime, double amplitude, double samplingFrequency, boolean isDiscrete) {
        super(values, startTime, endTime, amplitude, samplingFrequency, isDiscrete);
    }

    public RealSignal(double[] values, double startTime, double endTime, double amplitude, double samplingFrequency) {
        super(values, startTime, endTime, amplitude, samplingFrequency, true);
    }

    public RealSignal(double[] values, double startTime, double samplingFrequency) {
        super(values, startTime, samplingFrequency);
    }

    @Override
    public Signal add(Signal signal) {
        double samplingFrequency1 = this.samplingFrequency;
        double samplingFrequency2 = signal.getSamplingFrequency();
        double startTime1 = this.startTime;
        double startTime2 = signal.getStartTime();
        if (samplingFrequency1 - samplingFrequency2 > 0.000001 || Math.abs(startTime1 - startTime2) > 0.000001) {
            throw new IllegalArgumentException("Signals should have same sampling frequency and start time");
        }
        double[] values1 = this.values;
        double[] values2 = signal.getValues();
        int n = Math.min(values1.length, values2.length);
        double[] newValues = new double[n];
        for (int i = 0; i < n; i++) {
            newValues[i] = values1[i] + values2[i];
        }
        return new RealSignal(newValues, 0, samplingFrequency1);
    }

    @Override
    public Signal subtract(Signal signal) {
        return this.add(signal.negate());
    }

    @Override
    public Signal multiply(Signal signal) {
        double samplingFrequency1 = this.samplingFrequency;
        double samplingFrequency2 = signal.getSamplingFrequency();
        double startTime1 = this.startTime;
        double startTime2 = signal.getStartTime();
        if (samplingFrequency1 - samplingFrequency2 > 0.000001 || Math.abs(startTime1 - startTime2) > 0.000001) {
            throw new IllegalArgumentException("Signals should have same sampling frequency and start time");
        }
        double[] values1 = this.values;
        double[] values2 = signal.getValues();
        int n = Math.min(values1.length, values2.length);
        double[] newValues = new double[n];
        for (int i = 0; i < n; i++) {
            newValues[i] = values1[i] * values2[i];
        }
        return new RealSignal(newValues, 0, samplingFrequency1);
    }

    @Override
    public Signal divide(Signal signal) {
        return this.multiply(signal.inverse());
    }

    @Override
    public Signal negate() {
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = -values[i];
        }
        return new RealSignal(newValues, startTime, endTime, amplitude, samplingFrequency, isDiscrete);
    }

    @Override
    public Signal inverse() {
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = Math.abs(values[i]) < 0.0000000001 ? 0.0 : 1.0 / values[i];
        }
        return new RealSignal(newValues, startTime, endTime, amplitude, samplingFrequency, isDiscrete);
    }

    @Override
    public Signal reverse() {
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[values.length - 1 - i];
        }
        return new RealSignal(newValues, startTime, endTime, amplitude, samplingFrequency, isDiscrete);
    }

    @Override
    public Signal convolution(Signal signal) {
        double samplingFrequency1 = this.samplingFrequency;
        double samplingFrequency2 = signal.getSamplingFrequency();
        if (samplingFrequency1 - samplingFrequency2 > 0.000001) {
            throw new IllegalArgumentException("Signals should have same sampling frequency");
        }
        final double[] values1 = this.values;
        final double[] values2 = signal.getValues();
        double[] newValues = new double[values1.length + values2.length - 1];
        for (int i = 0; i < newValues.length; i++) {
            newValues[i] = 0.0;
            for (int k = 0; k < values1.length; k++) {
                if (i - k >= 0 && i - k < values2.length) {
                    newValues[i] += values1[k] * values2[i - k];
                }
            }
        }
        return new RealSignal(newValues, startTime, 1.0 / samplingFrequency * newValues.length, amplitude, samplingFrequency, true);
    }

    @Override
    public Signal correlation(Signal signal) {
        double samplingFrequency1 = this.samplingFrequency;
        double samplingFrequency2 = signal.getSamplingFrequency();
        if (samplingFrequency1 - samplingFrequency2 > 0.000001) {
            throw new IllegalArgumentException("Signals should have same sampling frequency");
        }
        final double[] values1 = this.values;
        final double[] values2 = signal.getValues();
        double[] newValues = new double[values1.length + values2.length - 1];
        for (int i = -newValues.length / 2; i < newValues.length / 2; i++) {
            newValues[i + newValues.length / 2] = 0.0;
            for (int k = 0; k < values1.length; k++) {
                if (i + k >= 0 && i + k < values2.length) {
                    newValues[i + newValues.length / 2] += values1[k] * values2[i + k];
                }
            }
        }
        double samplingPeriod = 1.0 / samplingFrequency1;
        double newEndTime = startTime + samplingPeriod * newValues.length;
        return new RealSignal(newValues, startTime, newEndTime, amplitude, samplingFrequency, true);
    }

    @Override
    public Signal correlationUsingConvolution(Signal signal) {
        return convolution(signal.reverse()).reverse();
    }

    @Override
    public double average() {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / (values.length + 1);
    }

    @Override
    public double averageMagnitude() {
        double sum = 0.0;
        for (double value : values) {
            sum += Math.abs(value);
        }
        return sum / values.length;
    }

    @Override
    public double power() {
        double sum = 0.0;
        for (double value : values) {
            sum += Math.pow(value, 2);
        }
        return sum / values.length;
    }

    @Override
    public double variance() {
        double avrg = average();
        double sum = 0.0;
        for (double value : values) {
            sum += Math.pow(value - avrg, 2);
        }
        return sum / values.length;
    }

    @Override
    public double effectiveValue() {
        return Math.sqrt(power());
    }


    @Override
    public String getType() {
        return "REAL";
    }

}
