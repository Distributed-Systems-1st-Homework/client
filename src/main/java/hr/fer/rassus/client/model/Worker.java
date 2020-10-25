package hr.fer.rassus.client.model;

import hr.fer.rassus.client.RestTemplateClient;
import hr.fer.rassus.client.SocketClient;
import hr.fer.rassus.client.constants.Constants;
import hr.fer.rassus.client.util.MeasurementsAverage;
import hr.fer.rassus.client.util.ReadDataFromFile;
import org.springframework.http.ResponseEntity;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private final Socket clientSocket;
    private final AtomicBoolean isRunning;
    private final AtomicInteger activeConnections;
    private RestTemplateClient clientToServer;
    String currentClientUsername;

    public Worker(Socket clientSocket, AtomicBoolean isRunning, AtomicInteger activeConnections,
                  RestTemplateClient clientToServer, String currentClientUsername) {
        this.clientSocket = clientSocket;
        this.isRunning = isRunning;
        this.activeConnections = activeConnections;
        this.clientToServer = clientToServer;
        this.currentClientUsername = currentClientUsername;
    }

    @Override
    public void run() {
        try (BufferedReader inFromOtherSensor = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             Scanner userInput = new Scanner(new InputStreamReader(System.in));)
        {
            SocketClient socketClient = new SocketClient();

            if(userInput.equals("USER_START_MEASURE")) {
                ReadDataFromFile reading = new ReadDataFromFile();
                String fileRow = reading.readFileRow(socketClient.getCsvFile());
                SensorMeasurement currentSensorMeasurement = reading.parseFileRow(fileRow);

                // Get closest neighbour
                ResponseEntity<UserAddress> closestNeighbourResponse = socketClient.getClientToServerConnection()
                        .findClosestNeighbour(socketClient.getClientUsername());
                UserAddress closestNeighbour = closestNeighbourResponse.getBody();

                // Create new connection socket to the closest sensor and create a writer
                Socket clientSocketWhenUserStarts = new Socket(InetAddress.getByName(closestNeighbour.getIpAddress()),
                        closestNeighbour.getPort(), clientSocket.getLocalAddress(), clientSocket.getLocalPort());
                PrintWriter outToSensorWhenUserStarts = new PrintWriter(new OutputStreamWriter(
                        clientSocketWhenUserStarts.getOutputStream()), true);

                // Start receiving data from the closest sensor
                outToSensorWhenUserStarts.print("SENSOR_START_MEASURE");
                final ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                MeasurementsAverage measurementsAverage = new MeasurementsAverage();
                Thread th = new Thread();
                new Thread(() -> {
                    while (true) {
                        if (userInput.hasNext() && userInput.equals("USER_STOP_MEASURE")) {
                            break;
                        }
                        else {
                            try {
                                // Read data from neighbour sensor and get average
                                SensorMeasurement closestNeighbourMeasurements =
                                        (SensorMeasurement) objectInputStream.readObject();
                                SensorMeasurement average = measurementsAverage.getAverage(currentSensorMeasurement,
                                        closestNeighbourMeasurements);

                                // Send average to the server
                                BeanInfo beanInfo = Introspector.getBeanInfo(SensorMeasurement.class);
                                for (PropertyDescriptor classAttribute : beanInfo.getPropertyDescriptors()) {
                                    String propertyName = classAttribute.getName();

                                    Measurement measurement = new Measurement();
                                    measurement.setUsername(this.currentClientUsername);
                                    measurement.setParameter(propertyName);
                                    switch (propertyName) {
                                        case "temperature" : {
                                            measurement.setAverageValue(average.getTemperature());
                                            break;
                                        }
                                        case "pressure" : {
                                            measurement.setAverageValue(average.getPressure());
                                            break;
                                        }
                                        case "humidity" : {
                                            measurement.setAverageValue(average.getHumidity());
                                            break;
                                        }
                                        case "co" : {
                                            measurement.setAverageValue(average.getCo());
                                            break;
                                        }
                                        case "no2" : {
                                            measurement.setAverageValue(average.getNo2());
                                            break;
                                        }
                                        case "so2" : {
                                            measurement.setAverageValue(average.getSo2());
                                            break;
                                        }
                                    }

                                    this.clientToServer.storeMeasurements(measurement);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (IntrospectionException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            if(inFromOtherSensor.readLine().equals("SENSOR_START_MEASURE")) {
                ReadDataFromFile reading = new ReadDataFromFile();
                String fileRow = reading.readFileRow(socketClient.getCsvFile());
                SensorMeasurement sensorMeasurement = reading.parseFileRow(fileRow);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                while(!inFromOtherSensor.readLine().equals("SENSOR_STOP_MEASURE")) {
                    // Send measurements
                    objectOutputStream.writeObject(sensorMeasurement);

                    // Read new row and parse it
                    fileRow = reading.readFileRow(socketClient.getCsvFile());
                    sensorMeasurement = reading.parseFileRow(fileRow);
                }
            }

            // Sensor is shut down
            this.isRunning.set(false);
            activeConnections.getAndDecrement();
        } catch (IOException ex) {
            System.out.println("Exception caught while trying to read or print out data" + ex);
        }
    }
}
