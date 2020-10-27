package hr.fer.rassus.client;

import hr.fer.rassus.client.api.SocketApi;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.WaitUserWorker;
import hr.fer.rassus.client.model.SensorWorker;
import hr.fer.rassus.client.util.RandomString;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.web.client.HttpStatusCodeException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
    // socket which gets given the connection of the client by server socket
    private AtomicInteger activeConnections;
    private ExecutorService executor;
    private AtomicBoolean runningFlag;
    private File csvFile;
    private String clientUsername;
    private double latitude;
    private double longitude;
    private RestTemplateClient clientToServer;

    public SocketClient(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        this.activeConnections = new AtomicInteger(0);
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        runningFlag = new AtomicBoolean(false);
    }

    public SocketClient() {}

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

            this.runningFlag.set(true);
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
        try {
            // Execute user worker in a separate thread
            Runnable userWorker = new WaitUserWorker(this.csvFile, this.clientToServer, this.clientUsername);
            new Thread(userWorker).start();

            // Execute client to client in a separate thread
            Socket socket = this.serverSocket.accept();
            Runnable worker = new SensorWorker(this.csvFile, this.clientUsername, this.activeConnections,
                    this.clientToServer, socket);

            this.executor.execute(worker);
            this.activeConnections.getAndIncrement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        // Shut down the sensor
        this.runningFlag.set(false);
        activeConnections.getAndDecrement();
    }
}
