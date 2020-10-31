package hr.fer.rassus.client;

import hr.fer.rassus.client.api.SocketApi;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.SensorListener;
import hr.fer.rassus.client.model.UserListener;
import hr.fer.rassus.client.util.RandomString;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.web.client.HttpStatusCodeException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class SocketClient implements SocketApi {
    private int port;
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
            // generate random username
            this.clientUsername = "Sensor_" + RandomString.generateRandomString(5);
            ClientApplication.logger.info("Sensor is named " + this.clientUsername);

            this.serverSocket = new ServerSocket(port);
            ClientApplication.logger.info("Sensor " + this.clientUsername + " starts on port " + this.port);

            // load the .csv file
            ReadDataFromFile reading = new ReadDataFromFile(this.clientUsername);
            csvFile = reading.loadCSVFile(Constants.FILE_PATH.getConstant());

            // generate random latitude and longitude
            this.latitude = Math.random() * (45.85 - 45.75) + 45.75;
            this.longitude = Math.random() * (16 - 15.87) + 15.87;
            ClientApplication.logger.info("Sensor " + this.clientUsername + " has latitude: " + this.latitude + " and" +
                    " longitude: " + this.longitude);

            // create instance of REST connection to server
            this.clientToServer = new RestTemplateClient("http://localhost:8080");

            // register on the server
            this.clientToServer.register(new SensorDescription(this.clientUsername, this.latitude, this.longitude,
                    "127.0.0.1", this.port));

        } catch (HttpStatusCodeException hsce) {
            if(hsce.getStatusCode().is2xxSuccessful()) {
                ClientApplication.logger.info("Sensor " + this.clientUsername + " is successfully registered");
            } else if(hsce.getStatusCode().is4xxClientError()) {
                ClientApplication.logger.error("Sensor " + this.clientUsername + " is already registered");
                System.exit(1);
            } else if(hsce.getStatusCode().is5xxServerError()) {
                ClientApplication.logger.error("Internal server error while registering sensor " + this.clientUsername);
                System.exit(1);
            } else {
                ClientApplication.logger.error("Unknown error while registering sensor " + this.clientUsername);
                System.exit(1);
            }
        } catch (IOException ioe) {
            ClientApplication.logger.error("Error with setting server socket");
            System.exit(1);
        }
    }

    @Override
    public void loop() {
        // Execute waiting for neighbour sensor in a separate thread
        Runnable waitSensorWorker = new SensorListener(this.csvFile, this.clientUsername, this.clientToServer, this.serverSocket);
        new Thread(waitSensorWorker).start();
        ClientApplication.logger.info("Listener for user input started");
        
        // Execute waiting for user input in a separate thread
        Runnable waitUserWorker = new UserListener(this.csvFile, this.clientToServer, this.clientUsername);
        new Thread(waitUserWorker).start();
        ClientApplication.logger.info("Listener for sensor connection started");
    }
}
