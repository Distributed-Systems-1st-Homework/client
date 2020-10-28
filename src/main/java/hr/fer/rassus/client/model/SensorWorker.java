package hr.fer.rassus.client.model;

import hr.fer.rassus.client.RestTemplateClient;
import hr.fer.rassus.client.util.ReadDataFromFile;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class SensorWorker implements Runnable {
    private RestTemplateClient clientToServer;
    private Socket serverSocket;
    private File csvFile;
    private String clientUsername;
    private boolean running;

    public SensorWorker(File csvFile, String clientUsername, RestTemplateClient clientToServer, Socket serverSocket) {
        this.clientToServer = clientToServer;
        this.serverSocket = serverSocket;
        this.csvFile = csvFile;
        this.clientUsername = clientUsername;
    }

    public SensorWorker() {}

    @Override
    public void run() {
        while (running) {
            try {
                // Read and retrieve measurement data
                ReadDataFromFile reading = new ReadDataFromFile();
                String fileRow = reading.readFileRow(this.csvFile);
                SensorMeasurement sensorMeasurement = reading.parseFileRow(fileRow);

                // Send measurements
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.serverSocket.getOutputStream());
                objectOutputStream.writeObject(sensorMeasurement);
                Thread.sleep(5000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        this.running = false;
    }
}
