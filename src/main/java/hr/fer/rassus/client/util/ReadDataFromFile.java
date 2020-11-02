package hr.fer.rassus.client.util;

import hr.fer.rassus.client.ClientApplication;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.model.SensorMeasurement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

public class ReadDataFromFile {
    private String clientUsername;

    public ReadDataFromFile(String clientUsername) {
        this.clientUsername = clientUsername;
    }

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
            ClientApplication.logger.info("Sensor " + this.clientUsername + " has read values: temperature(" +
                    sensorParameter.getTemperature() + "), pressure(" + sensorParameter.getPressure() +
                    "), humidity(" + sensorParameter.getHumidity() + "), CO(" + sensorParameter.getCo() + "), NO2("
                    + sensorParameter.getNo2() + "), SO2(" + sensorParameter.getSo2() + ")");

            return sensorParameter;
        }

        ClientApplication.logger.error("Could not parse file for sensor " + this.clientUsername);
        return null;
    }

    public String readFileRow(File measurementsFile) {
        long currentTime = System.currentTimeMillis();
        long activeSeconds = currentTime - ClientApplication.START_TIME;
        ClientApplication.logger.info("Sensor " + this.clientUsername + " has been active for " + TimeUnit.MILLISECONDS.toSeconds(
                toIntExact(activeSeconds)) + " seconds");
        int fileRow = toIntExact(activeSeconds) % 100 + 1;

        ClientApplication.logger.info("Sensor " + this.clientUsername + " has read file row [" + fileRow + "]");

        try {
            String line = Files.readAllLines(measurementsFile.toPath()).get(fileRow);
            return line;
        } catch (IOException e) {
            return null;
        }
    }

    public File loadCSVFile (String filePath) {
        if(filePath != null) {
            File measurementsFile = new File(filePath);
            System.out.println(measurementsFile.getAbsolutePath());
            return measurementsFile;
        }

        return null;
    }
}
