package hr.fer.rassus.client.model;

import hr.fer.rassus.client.ClientApplication;
import hr.fer.rassus.client.RestTemplateClient;
import hr.fer.rassus.client.util.MeasurementsAverage;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserWorker implements Runnable {
    private Socket socket;
    private File csvFile;
    private RestTemplateClient clientToServer;
    private String clientUsername;
    private AtomicBoolean running;
    private BufferedReader in;
    private PrintWriter out;

    public UserWorker(File csvFile, RestTemplateClient clientToServer, String clientUsername) {
        this.csvFile = csvFile;
        this.clientToServer = clientToServer;
        this.clientUsername = clientUsername;
        this.running = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        try {
            // Get closest neighbour
            ResponseEntity<UserAddress> closestNeighbourResponse = this.clientToServer.findClosestNeighbour(this.clientUsername);
            UserAddress closestNeighbour = closestNeighbourResponse.getBody();

            // Create new connection socket to the closest sensor and create a writer and a reader
            this.socket = new Socket(closestNeighbour.getIpAddress(), closestNeighbour.getPort());
            this.socket.setKeepAlive(true);
            this.out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            while (this.running.get()) {
                // Read file row
                ReadDataFromFile reading = new ReadDataFromFile(this.clientUsername);
                String fileRow = reading.readFileRow(this.csvFile);
                SensorMeasurement currentSensorMeasurement = reading.parseFileRow(fileRow);

                // Invoke the closest sensor
                this.out.println("SENSOR_START_MEASURE");

                // Read data from closest sensor and get average
                String input = in.readLine();
                String[] measurements = input.split("\\|");

                SensorMeasurement closestNeighbourMeasurements = new SensorMeasurement();
                closestNeighbourMeasurements.setTemperature(measurements[0].equals("null") ? null : Float.valueOf(measurements[0]));
                closestNeighbourMeasurements.setPressure(measurements[1].equals("null") ? null : Float.valueOf(measurements[1]));
                closestNeighbourMeasurements.setHumidity(measurements[2].equals("null") ? null : Float.valueOf(measurements[2]));
                closestNeighbourMeasurements.setCo(measurements[3].equals("null") ? null : Float.valueOf(measurements[3]));
                closestNeighbourMeasurements.setNo2(measurements[4].equals("null") ? null : Float.valueOf(measurements[4]));
                closestNeighbourMeasurements.setSo2(measurements[5].equals("null") ? null : Float.valueOf(measurements[5]));

                ClientApplication.logger.info("Sensor " + this.clientUsername + " receives data: " + closestNeighbourMeasurements);

                // Get average
                MeasurementsAverage measurementsAverage = new MeasurementsAverage();
                SensorMeasurement average = measurementsAverage.getAverage(currentSensorMeasurement, closestNeighbourMeasurements);
                ClientApplication.logger.info("Sensor " + this.clientUsername + " calculates average data: " + average);
                // Make a Measurement object and send it to the server
                for (Field field : average.getClass().getDeclaredFields()) {
                    Measurement measurement = new Measurement();
                    measurement.setUsername(this.clientUsername);
                    measurement.setParameter(field.getName());
                    field.setAccessible(true);
                    measurement.setAverageValue((Float)field.get(average));

                    if(measurement.getAverageValue() != null) {
                        if(measurement.getAverageValue() != 0.0) {
                            this.clientToServer.storeMeasurements(measurement);
                            ClientApplication.logger.info("Sensor " + this.clientUsername + " sent parameter \"" + field.getName() +
                                    "\": " + (Float)field.get(average) + " to the server");
                        }
                    }
                }
            }

            // CLose socket connection
            this.out.println("SENSOR_STOP_MEASURE");
            this.socket.close();
        } catch (IOException e) {
            ClientApplication.logger.info("Neighbour sensor ended socket connection");
            terminate();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (HttpClientErrorException hcee) {
            if(hcee.getStatusCode().is4xxClientError()) {
                ClientApplication.logger.error("No other active sensor found");
                terminate();
            }
        } catch (NullPointerException ne) {
            ClientApplication.logger.error("Sensor \"" + this.clientUsername + "\" could not read or parse file");
            terminate();
        }
    }

    public void terminate() {
        this.running.set(false);
    }
}

