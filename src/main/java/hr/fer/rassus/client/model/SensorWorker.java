package hr.fer.rassus.client.model;

import hr.fer.rassus.client.ClientApplication;
import hr.fer.rassus.client.util.ReadDataFromFile;
import java.io.*;
import java.net.Socket;

public class SensorWorker implements Runnable {
    private String clientUsername;
    private Socket serverSocket;
    private File csvFile;
    private PrintWriter out;

    public SensorWorker(File csvFile, Socket serverSocket, String clientUsername) {
        this.clientUsername = clientUsername;
        this.serverSocket = serverSocket;
        this.csvFile = csvFile;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(new OutputStreamWriter(this.serverSocket.getOutputStream()), true);
            BufferedReader inFromOtherSensor = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));

            ClientApplication.logger.info("Sensor " + this.clientUsername + " received request to start sending data");
            while (true) {
                String input = inFromOtherSensor.readLine();
                if(input.equals("SENSOR_START_MEASURE")) {
                    // Read and retrieve measurement
                    Thread.sleep(4000);
                    ReadDataFromFile reading = new ReadDataFromFile(this.clientUsername);
                    String fileRow = reading.readFileRow(this.csvFile);
                    SensorMeasurement sensorMeasurement = reading.parseFileRow(fileRow);

                    // Send measurements
                    ClientApplication.logger.info("Sensor " + this.clientUsername + " sends data: " + sensorMeasurement);
                    this.out.println(sensorMeasurement.getTemperature() + "|" + sensorMeasurement.getPressure() + "|" +
                            sensorMeasurement.getHumidity() + "|" + sensorMeasurement.getCo() + "|" +
                            sensorMeasurement.getNo2() + "|" + sensorMeasurement.getSo2());
                } else if(input.equals("SENSOR_STOP_MEASURE")) {
                    ClientApplication.logger.info("Sensor " + this.clientUsername + " stops sending data");
                    break;
                }
            }

        } catch (IOException | InterruptedException e) {
            ClientApplication.logger.info("Sensor " + this.clientUsername + " received closed socket connection exception");
        }
    }
}
