package hr.fer.rassus.client.model;

import hr.fer.rassus.client.ClientApplication;
import hr.fer.rassus.client.RestTemplateClient;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SensorListener implements Runnable {
    private RestTemplateClient clientToServer;
    private ServerSocket serverSocket;
    private File csvFile;
    private String clientUsername;

    public SensorListener(File csvFile, String clientUsername, RestTemplateClient clientToServer,
                          ServerSocket serverSocket) {
        this.clientToServer = clientToServer;
        this.serverSocket = serverSocket;
        this.csvFile = csvFile;
        this.clientUsername = clientUsername;
    }

    @Override
    public void run() {
        while(true) {
            Socket socket = null;
            try {
                socket = this.serverSocket.accept();
                ClientApplication.logger.info("Sensor " + this.clientUsername + " started a new sensor worker thread");
            } catch (IOException e) {
                ClientApplication.logger.error("I/O exception on sensor " + this.clientUsername + " while listening for socket " +
                        "connection request");
            }

            SensorWorker sensorWorker = new SensorWorker(this.csvFile, socket, this.clientUsername);
            new Thread(sensorWorker).start();
        }
    }
}
