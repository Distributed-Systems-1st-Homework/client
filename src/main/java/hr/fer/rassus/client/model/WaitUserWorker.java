package hr.fer.rassus.client.model;

import hr.fer.rassus.client.RestTemplateClient;
import java.io.*;
import java.util.Scanner;

public class WaitUserWorker implements Runnable {
    private File csvFile;
    private RestTemplateClient clientToServer;
    private String clientUsername;

    public WaitUserWorker(File csvFile, RestTemplateClient clientToServer, String clientUsername) {
        this.csvFile = csvFile;
        this.clientToServer = clientToServer;
        this.clientUsername = clientUsername;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Thread thread;

        while(true) {
            if(scanner.hasNext() && scanner.equals("USER_START_MEASURE")) {
                Runnable userWorker = new UserWorker(this.csvFile, this.clientToServer, this.clientUsername);
                thread = new Thread(userWorker);
                thread.start();
            } else if(scanner.hasNext() && scanner.equals("USER_STOP_MEASURE")) {
                new UserWorker().terminate();
            }
        }
    }
}

