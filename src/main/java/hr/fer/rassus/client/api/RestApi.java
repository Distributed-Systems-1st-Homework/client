package hr.fer.rassus.client.api;

import hr.fer.rassus.client.model.Measurement;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.UserAddress;
import org.springframework.http.ResponseEntity;

public interface RestApi {
    ResponseEntity<String> register(SensorDescription sensorDescription);
    ResponseEntity<UserAddress> findClosestNeighbour(String username);
    ResponseEntity<String> storeMeasurements(Measurement measurement);
}
