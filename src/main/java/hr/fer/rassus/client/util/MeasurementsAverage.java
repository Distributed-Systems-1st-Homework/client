package hr.fer.rassus.client.util;

import hr.fer.rassus.client.model.SensorMeasurement;

public class MeasurementsAverage {
    public SensorMeasurement getAverage(SensorMeasurement currentSensor, SensorMeasurement neighbourSensor) {
        SensorMeasurement average = new SensorMeasurement();

        Float temperature = (currentSensor.getTemperature()  + neighbourSensor.getTemperature()) / 2;
        Float pressure = (currentSensor.getPressure() + neighbourSensor.getPressure()) / 2;
        Float humidity = (currentSensor.getHumidity() + neighbourSensor.getHumidity()) / 2;
        Float co = (currentSensor.getCo() + neighbourSensor.getCo()) / 2;
        Float no2 = ((currentSensor.getNo2() != null ? currentSensor.getNo2() : 0) +
                (neighbourSensor.getNo2() != null ? neighbourSensor.getNo2() : 0)) / 2;
        Float so2 = ((currentSensor.getSo2() != null ? currentSensor.getSo2() : 0) +
                (neighbourSensor.getSo2() != null ? neighbourSensor.getSo2() : 0)) / 2;

        average.setTemperature(temperature);
        average.setPressure(pressure);
        average.setHumidity(humidity);
        average.setCo(co);
        average.setNo2(no2);
        average.setSo2(so2);

        return average;
    }
}
