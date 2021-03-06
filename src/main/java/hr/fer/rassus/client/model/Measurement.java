package hr.fer.rassus.client.model;

public class Measurement {
    private String username;
    private String parameter;
    private Float averageValue;

    public Measurement(String username, String parameter, float averageValue) {
        this.username = username;
        this.parameter = parameter;
        this.averageValue = averageValue;
    }

    public Measurement() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Float getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(Float averageValue) {
        this.averageValue = averageValue;
    }
}
