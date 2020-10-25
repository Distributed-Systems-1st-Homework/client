package hr.fer.rassus.client;

import hr.fer.rassus.client.api.SocketApi;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.Worker;
import hr.fer.rassus.client.util.RandomString;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
    private Socket socket;
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
            this.runningFlag.set(true);

            // load the .csv file
            ReadDataFromFile reading = new ReadDataFromFile();
            csvFile = reading.loadCSVFile(Constants.FILE_PATH.getConstant());

            // generate random latitude and longitude
            this.latitude = Math.random() * (45.85 - 45.75) + 45.75;
            this.longitude = Math.random() * (16 - 15.87) + 15.87;

            // generate random username
            this.clientUsername = "Client_" + RandomString.generateRandomString(5);

            // create instance of REST connection to server
            clientToServer = new RestTemplateClient("http://localhost:8080");

            // register on the server
            ResponseEntity<String> response = clientToServer.register(new SensorDescription(this.clientUsername,
                    this.latitude, this.longitude, this.serverSocket.getLocalSocketAddress().toString(), this.port));

        } catch (SocketException e1) {
            System.err.println("Exception caught when setting server socket timeout: " + e1);
        } catch (IOException ex) {
            System.err.println("Exception caught when opening or setting the server socket: " + ex);
        }
    }

    @Override
    public void loop() {
        while (runningFlag.get()) {
            try {
                // create a new socket, accept and listen for a connection made to this socket
                Socket clientSocket = serverSocket.accept();
                // execute a tcp request handler in a new thread
                Runnable worker = new Worker(clientSocket, runningFlag, activeConnections, clientToServer, clientUsername);
                this.executor.execute(worker);
                activeConnections.getAndIncrement();
            } catch (SocketTimeoutException ste) {
                // do nothing, check runningFlag
            } catch (IOException ex) {
                System.err.println("Exception caught when waiting for a connection: " + ex);
            }
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean getRunningFlag() {
        return false;
    }

    public File getCsvFile() {
        return this.csvFile;
    }

    public RestTemplateClient getClientToServerConnection() {
        return this.clientToServer;
    }

    public String getClientUsername() {
        return this.clientUsername;
    }
}
