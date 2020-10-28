package hr.fer.rassus.client;

import hr.fer.rassus.client.api.SocketApi;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.WaitSensorWorker;
import hr.fer.rassus.client.model.WaitUserWorker;
import hr.fer.rassus.client.util.RandomString;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketClient implements SocketApi {
    private int port = 0;
    private int backlog = 10;
    private int NUMBER_OF_THREADS = 4;
    // socket which listens for incoming connections
    private ServerSocket serverSocket;
    private File csvFile;
    private String clientUsername;
    private double latitude;
    private double longitude;
    private RestTemplateClient clientToServer;

    public SocketClient(int port) {
        this.port = port;
    }

    @Override
    public void startup() {
        try {
            this.serverSocket = new ServerSocket(port, backlog);
            this.serverSocket.setSoTimeout(500);

            // load the .csv file
            ReadDataFromFile reading = new ReadDataFromFile();
            csvFile = reading.loadCSVFile(Constants.FILE_PATH.getConstant());

            // generate random latitude and longitude
            this.latitude = Math.random() * (45.85 - 45.75) + 45.75;
            this.longitude = Math.random() * (16 - 15.87) + 15.87;

            // generate random username
            this.clientUsername = "Client_" + RandomString.generateRandomString(5);

            // create instance of REST connection to server
            this.clientToServer = new RestTemplateClient("http://localhost:8080");

            // register on the server
            this.clientToServer.register(new SensorDescription(this.clientUsername, this.latitude, this.longitude,
                    this.serverSocket.getLocalSocketAddress().toString(), this.port));

        } catch (HttpStatusCodeException hsce) {
            System.err.println("Sensor is already registered: " + hsce);
        } catch (SocketException se) {
            System.err.println("Exception caught when setting server socket timeout: " + se);
        } catch (IOException ioe) {
            System.err.println("Exception caught when opening or setting the server socket: " + ioe);
        }
    }

    @Override
    public void loop() {
        // Execute waiting for user input in a separate thread
        Runnable waitUserWorker = new WaitUserWorker(this.csvFile, this.clientToServer, this.clientUsername);
        new Thread(waitUserWorker).start();

        // Execute waiting for neighbour sensor in a separate thread
        Runnable waitSensorWorker = new WaitSensorWorker(this.csvFile, this.clientUsername, this.clientToServer,
                this.serverSocket);
    }
}
