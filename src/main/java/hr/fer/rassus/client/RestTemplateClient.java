package hr.fer.rassus.client;

import hr.fer.rassus.client.api.RestApi;
import hr.fer.rassus.client.model.Measurement;
import hr.fer.rassus.client.model.SensorDescription;
import hr.fer.rassus.client.model.UserAddress;
import hr.fer.rassus.client.request.SensorNameDto;
import org.springframework.http.HttpEntity;
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
        System.out.println(sensorDescription);
        HttpEntity<SensorDescription> request = new HttpEntity<>(sensorDescription);
        ResponseEntity<String> response = restTemplate.postForEntity(baseURI + "/registration",
                request, String.class);

        return response;
    }

    @Override
    public ResponseEntity<UserAddress> findClosestNeighbour(String username) {
        HttpEntity<SensorNameDto> request = new HttpEntity<>(new SensorNameDto(username));
        ResponseEntity<UserAddress> response = restTemplate.postForEntity(baseURI + "/closest",
                request, UserAddress.class);

        return response;
    }

    @Override
    public ResponseEntity<String> storeMeasurements(Measurement measurement) {
        HttpEntity<Measurement> request = new HttpEntity<>(measurement);
        ResponseEntity<String> response = restTemplate.postForEntity(baseURI + "/measurement",
                request, String.class);

        return response;
    }

    @Override
    public ResponseEntity<String> shutdown(String username) {
        HttpEntity<SensorNameDto> request = new HttpEntity<>(new SensorNameDto(username));
        ResponseEntity<String> response = restTemplate.postForEntity(baseURI + "/shutdown",
                request, String.class);

        return response;
    }
}
