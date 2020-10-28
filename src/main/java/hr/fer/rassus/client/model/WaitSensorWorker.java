package hr.fer.rassus.client.model;

import hr.fer.rassus.client.RestTemplateClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitSensorWorker implements Runnable {
    private RestTemplateClient clientToServer;
    private ServerSocket serverSocket;
    private File csvFile;
    private String clientUsername;

    public WaitSensorWorker(File csvFile, String clientUsername, RestTemplateClient clientToServer,
                            ServerSocket serverSocket) {
        this.clientToServer = clientToServer;
        this.serverSocket = serverSocket;
        this.csvFile = csvFile;
        this.clientUsername = clientUsername;
    }

    @Override
    public void run() {
        Socket socket = null;

        try {
            socket = this.serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thread = null;
        try (BufferedReader inFromOtherSensor = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
                if(inFromOtherSensor.readLine().equals("SENSOR_START_MEASURE")) {
                    Runnable sensorWorker = new SensorWorker(this.csvFile, this.clientUsername, this.clientToServer, socket);
                    thread = new Thread(sensorWorker);
                    thread.start();
                } else if(inFromOtherSensor.readLine().equals("SENSOR_STOP_MEASURE")) {
                    new SensorWorker().terminate();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
