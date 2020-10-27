package hr.fer.rassus.client.model;

import hr.fer.rassus.client.RestTemplateClient;
import hr.fer.rassus.client.SocketClient;
import hr.fer.rassus.client.util.ReadDataFromFile;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class SensorWorker implements Runnable {
    private final AtomicInteger activeConnections;
    private RestTemplateClient clientToServer;
    private Socket serverSocket;
    private File csvFile;
    private String clientUsername;

    public SensorWorker(File csvFile, String clientUsername, AtomicInteger activeConnections,
                        RestTemplateClient clientToServer, Socket serverSocket) {
        this.activeConnections = activeConnections;
        this.clientToServer = clientToServer;
        this.serverSocket = serverSocket;
        this.csvFile = csvFile;
        this.clientUsername = clientUsername;
    }

    @Override
    public void run() {
        while(true) {
            try (BufferedReader inFromOtherSensor = new BufferedReader(
                    new InputStreamReader(serverSocket.getInputStream()));)
            {
                if(inFromOtherSensor.readLine().equals("SENSOR_START_MEASURE")) {
                    while(!inFromOtherSensor.readLine().equals("SENSOR_STOP_MEASURE")) {
                        // Read and retrieve measurement data
                        ReadDataFromFile reading = new ReadDataFromFile();
                        String fileRow = reading.readFileRow(this.csvFile);
                        SensorMeasurement sensorMeasurement = reading.parseFileRow(fileRow);

                        // Send measurements
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.serverSocket.getOutputStream());
                        objectOutputStream.writeObject(sensorMeasurement);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
