package com.cps.signalprocessing.repository;

import com.cps.signalprocessing.model.RealSignal;
import com.cps.signalprocessing.model.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SignalFileDao implements Dao<Signal> {

    private final Logger logger = LoggerFactory.getLogger(SignalFileDao.class);

    @Override
    public Signal read(String path) {
        List<Double> numbers = new ArrayList<>();
        File file = new File(path);
        BufferedReader reader = null;
        String signalType;
        try {
            reader = new BufferedReader(new FileReader(file));
            signalType = reader.readLine();
            String text;
            while ((text = reader.readLine()) != null) {
                numbers.add(Double.parseDouble(text));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        double startTime = numbers.get(0);
        double samplingFrequency = numbers.get(1);
        double[] values = new double[numbers.size() - 2];
        try {
            for (int i = 2; i < numbers.size(); i++) {
                values[i - 2] = numbers.get(i);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return new RealSignal(values, startTime, samplingFrequency);
    }

    @Override
    public void write(Signal obj, String path) {
        File file = new File(path);
        String signalType = obj.getType();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(signalType + "\n");
            writer.write(Double.toString(obj.getStartTime()) + "\n");
            writer.write(Double.toString(obj.getSamplingFrequency()) + "\n");
            double[] values = obj.getValues();
            for (double value : values) {
                writer.write(Double.toString(value) + "\n");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

}
