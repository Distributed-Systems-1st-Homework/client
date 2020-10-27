package hr.fer.rassus.client.util;

import hr.fer.rassus.client.model.SensorMeasurement;

public class MeasurementsAverage {
    private Float temperature;
    private Float pressure;
    private Float humidity;
    private Float co;
    private Float no2;
    private Float so2;

    public SensorMeasurement getAverage(SensorMeasurement currentSensor, SensorMeasurement neighbourSensor) {
        SensorMeasurement average = new SensorMeasurement();

        if(currentSensor.getTemperature() == null || currentSensor.getTemperature() == 0) {
            this.temperature = neighbourSensor.getTemperature();
        } else if(neighbourSensor.getTemperature() == null || neighbourSensor.getTemperature() == 0) {
            this.temperature = currentSensor.getTemperature();
        } else {
            this.temperature = (currentSensor.getTemperature() + neighbourSensor.getTemperature()) / 2;
        }

        if(currentSensor.getPressure() == null || currentSensor.getPressure() == 0) {
            this.temperature = neighbourSensor.getPressure();
        } else if(neighbourSensor.getPressure() == null || neighbourSensor.getPressure() == 0) {
            this.temperature = currentSensor.getPressure();
        } else {
            this.temperature = (currentSensor.getPressure() + neighbourSensor.getPressure()) / 2;
        }

        if(currentSensor.getHumidity() == null || currentSensor.getHumidity() == 0) {
            this.temperature = neighbourSensor.getHumidity();
        } else if(neighbourSensor.getHumidity() == null || neighbourSensor.getHumidity() == 0) {
            this.temperature = currentSensor.getHumidity();
        } else {
            this.temperature = (currentSensor.getHumidity() + neighbourSensor.getHumidity()) / 2;
        }

        if(currentSensor.getCo() == null || currentSensor.getCo() == 0) {
            this.temperature = neighbourSensor.getCo();
        } else if(neighbourSensor.getCo() == null || neighbourSensor.getCo() == 0) {
            this.temperature = currentSensor.getCo();
        } else {
            this.temperature = (currentSensor.getCo() + neighbourSensor.getCo()) / 2;
        }

        if(currentSensor.getNo2() == null || currentSensor.getNo2() == 0) {
            this.temperature = neighbourSensor.getNo2();
        } else if(neighbourSensor.getNo2() == null || neighbourSensor.getNo2() == 0) {
            this.temperature = currentSensor.getNo2();
        } else {
            this.temperature = (currentSensor.getNo2() + neighbourSensor.getNo2()) / 2;
        }

        if(currentSensor.getSo2() == null || currentSensor.getSo2() == 0) {
            this.temperature = neighbourSensor.getSo2();
        } else if(neighbourSensor.getSo2() == null || neighbourSensor.getSo2() == 0) {
            this.temperature = currentSensor.getSo2();
        } else {
            this.temperature = (currentSensor.getSo2() + neighbourSensor.getSo2()) / 2;
        }

        average.setTemperature(this.temperature);
        average.setPressure(this.pressure);
        average.setHumidity(this.humidity);
        average.setCo(this.co);
        average.setNo2(this.no2);
        average.setSo2(this.so2);

        return average;
    }
}
