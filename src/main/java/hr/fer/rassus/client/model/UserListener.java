package hr.fer.rassus.client.model;

import hr.fer.rassus.client.ClientApplication;
import hr.fer.rassus.client.RestTemplateClient;
import java.io.*;
import java.util.Scanner;

public class UserListener implements Runnable {
    private File csvFile;
    private RestTemplateClient clientToServer;
    private String clientUsername;

    public UserListener(File csvFile, RestTemplateClient clientToServer, String clientUsername) {
        this.csvFile = csvFile;
        this.clientToServer = clientToServer;
        this.clientUsername = clientUsername;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        UserWorker userWorker = null;
        while(true) {
            String input = scanner.nextLine();
            if(input.equals("START")) {
                userWorker = new UserWorker(this.csvFile, this.clientToServer, this.clientUsername);
                new Thread(userWorker).start();
                ClientApplication.logger.info("Sensor " + this.clientUsername + " started a new user worker thread");
            } else if(input.equals("STOP")) {
                userWorker.terminate();
            }
        }
    }
}

