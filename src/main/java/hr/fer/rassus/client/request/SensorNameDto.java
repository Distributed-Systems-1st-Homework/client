package hr.fer.rassus.client.request;

public class SensorNameDto {
    String username;

    public SensorNameDto(String username) {
        this.username = username;
    }

    public SensorNameDto() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "RegisterUsernameDto{" +
                "username='" + username + '\'' +
                '}';
    }
}
