package hr.fer.rassus.client.util;

import hr.fer.rassus.client.model.SensorMeasurement;

public class MeasurementsAverage {
    public SensorMeasurement getAverage(SensorMeasurement currentSensor, SensorMeasurement neighbourSensor) {
        SensorMeasurement average = new SensorMeasurement();

        Float temperature;
        if(currentSensor.getTemperature() == null || currentSensor.getTemperature() == 0) {
            temperature = neighbourSensor.getTemperature();
        } else if(neighbourSensor.getTemperature() == null || neighbourSensor.getTemperature() == 0) {
            temperature = currentSensor.getTemperature();
        } else {
            temperature = (currentSensor.getTemperature() + neighbourSensor.getTemperature()) / 2;
        }

        Float pressure;
        if(currentSensor.getPressure() == null || currentSensor.getPressure() == 0) {
            pressure = neighbourSensor.getPressure();
        } else if(neighbourSensor.getPressure() == null || neighbourSensor.getPressure() == 0) {
            pressure = currentSensor.getPressure();
        } else {
            pressure = (currentSensor.getPressure() + neighbourSensor.getPressure()) / 2;
        }

        Float humidity;
        if(currentSensor.getHumidity() == null || currentSensor.getHumidity() == 0) {
            humidity = neighbourSensor.getHumidity();
        } else if(neighbourSensor.getHumidity() == null || neighbourSensor.getHumidity() == 0) {
            humidity = currentSensor.getHumidity();
        } else {
            humidity = (currentSensor.getHumidity() + neighbourSensor.getHumidity()) / 2;
        }

        Float co;
        if(currentSensor.getCo() == null || currentSensor.getCo() == 0) {
            co = neighbourSensor.getCo();
        } else if(neighbourSensor.getCo() == null || neighbourSensor.getCo() == 0) {
            co = currentSensor.getCo();
        } else {
            co = (currentSensor.getCo() + neighbourSensor.getCo()) / 2;
        }

        Float no2;
        if(currentSensor.getNo2() == null || currentSensor.getNo2() == 0) {
            no2 = neighbourSensor.getNo2();
        } else if(neighbourSensor.getNo2() == null || neighbourSensor.getNo2() == 0) {
            no2 = currentSensor.getNo2();
        } else {
            no2 = (currentSensor.getNo2() + neighbourSensor.getNo2()) / 2;
        }

        Float so2;
        if(currentSensor.getSo2() == null || currentSensor.getSo2() == 0) {
            so2 = neighbourSensor.getSo2();
        } else if(neighbourSensor.getSo2() == null || neighbourSensor.getSo2() == 0) {
            so2 = currentSensor.getSo2();
        } else {
            so2 = (currentSensor.getSo2() + neighbourSensor.getSo2()) / 2;
        }

        average.setTemperature(temperature);
        average.setPressure(pressure);
        average.setHumidity(humidity);
        average.setCo(co);
        average.setNo2(no2);
        average.setSo2(so2);

        return average;
    }
}
