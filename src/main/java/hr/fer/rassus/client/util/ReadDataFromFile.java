package hr.fer.rassus.client.util;

import hr.fer.rassus.client.ClientApplication;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.model.SensorMeasurement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadDataFromFile {
    public SensorMeasurement parseFileRow(String fileRow) {
        Pattern pattern = Pattern.compile(Constants.PARSER_REGEX.getConstant());
        Matcher matcher = pattern.matcher(fileRow);

        if (matcher.find()) {
            SensorMeasurement sensorParameter = new SensorMeasurement();
            sensorParameter.setTemperature(!matcher.group(1).equals("") ? Float.valueOf(matcher.group(1)) : null);
            sensorParameter.setPressure(!matcher.group(2).equals("") ? Float.valueOf(matcher.group(2)) : null);
            sensorParameter.setHumidity(!matcher.group(3).equals("") ? Float.valueOf(matcher.group(3)) : null);
            sensorParameter.setCo(!matcher.group(4).equals("") ? Float.valueOf(matcher.group(4)) : null);
            sensorParameter.setNo2(!matcher.group(5).equals("") ? Float.valueOf(matcher.group(5)) : null);
            sensorParameter.setSo2(!matcher.group(6).equals("") ? Float.valueOf(matcher.group(6)) : null);

            return sensorParameter;
        }

        return null;
    }

    public String readFileRow(File measurementsFile) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long currentTime = System.currentTimeMillis();
        int fileRow = (int)((currentTime - ClientApplication.START_TIME) % 100 + 2);

        try {
            String line = Files.readAllLines(measurementsFile.toPath()).get(fileRow);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File loadCSVFile (String filePath) {
        if(filePath != null) {
            File measurementsFile = new File(filePath);
            return measurementsFile;
        }

        return null;
    }
}
