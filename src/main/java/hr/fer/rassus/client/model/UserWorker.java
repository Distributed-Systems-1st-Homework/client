package hr.fer.rassus.client.model;

import hr.fer.rassus.client.RestTemplateClient;
import hr.fer.rassus.client.util.MeasurementsAverage;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.http.ResponseEntity;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserWorker implements Runnable {
    private Socket socket;
    private File csvFile;
    private RestTemplateClient clientToServer;
    private String clientUsername;
    private boolean running;

    public UserWorker(File csvFile, RestTemplateClient clientToServer, String clientUsername) {
        this.csvFile = csvFile;
        this.clientToServer = clientToServer;
        this.clientUsername = clientUsername;
    }

    public UserWorker() {}

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while(running) {
            if(scanner.hasNext() && scanner.equals("USER_START_MEASURE")) {
                // Read and retrieve measurement data
                ReadDataFromFile reading = new ReadDataFromFile();
                String fileRow = reading.readFileRow(this.csvFile);
                SensorMeasurement currentSensorMeasurement = reading.parseFileRow(fileRow);

                // Get closest neighbour
                ResponseEntity<UserAddress> closestNeighbourResponse = this.clientToServer
                        .findClosestNeighbour(this.clientUsername);
                UserAddress closestNeighbour = closestNeighbourResponse.getBody();

                try {
                    // Create new connection socket to the closest sensor and create a writer
                    this.socket = new Socket(InetAddress.getByName(closestNeighbour.getIpAddress()),
                            closestNeighbour.getPort());
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()),
                            true);

                    // Invoke the closest sensor
                    out.print("SENSOR_START_MEASURE");
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    // Read data from closest sensor and get average
                    SensorMeasurement closestNeighbourMeasurements =
                            (SensorMeasurement) objectInputStream.readObject();
                    MeasurementsAverage measurementsAverage = new MeasurementsAverage();
                    SensorMeasurement average = measurementsAverage.getAverage(currentSensorMeasurement,
                            closestNeighbourMeasurements);

                    // Make a Measurement object and send it to the server
                    for (Field field : average.getClass().getDeclaredFields()) {
                        Measurement measurement = new Measurement();
                        measurement.setUsername(this.clientUsername);
                        measurement.setParameter(field.getName());
                        measurement.setAverageValue(field.getFloat(average));

                        this.clientToServer.storeMeasurements(measurement);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void terminate() {
        this.running = false;
    }
}

