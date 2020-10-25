package hr.fer.rassus.client;

import hr.fer.rassus.client.api.RestApi;
import hr.fer.rassus.client.model.Measurement;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.UserAddress;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestTemplateClient implements RestApi {
    private String baseURI;
    private RestTemplate restTemplate;

    public RestTemplateClient(String uri) {
        this.baseURI = uri;
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public ResponseEntity<String> register(SensorDescription sensorDescription) {
        ResponseEntity<String> response = restTemplate.postForEntity(baseURI + "/registration",
                sensorDescription, String.class);

        return response;
    }

    @Override
    public ResponseEntity<UserAddress> findClosestNeighbour(String username) {
        ResponseEntity<UserAddress> response = restTemplate.postForEntity(baseURI + "/closest",
                username, UserAddress.class);

        return response;
    }

    @Override
    public ResponseEntity<String> storeMeasurements(Measurement measurement) {
        ResponseEntity<String> response = restTemplate.postForEntity(baseURI + "/measurements",
                measurement, String.class);

        return response;
    }
}
