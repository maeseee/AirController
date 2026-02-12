package org.air_controller.sensor_values;

import lombok.Getter;

import java.util.function.Function;

@Getter
public enum MeasuredValue {
    TEMPERATURE("Temperature (°C)", dataPoint -> dataPoint.temperature().celsius()),
    HUMIDITY("Humidity (%)", dataPoint -> dataPoint.humidity().getRelativeHumidity(dataPoint.temperature())),
    CO2("CO2 (ppm)", dataPoint -> dataPoint.co2().map(CarbonDioxide::ppm).orElse(null));

    private final String nameWithUnit;
    private final Function<ClimateDataPoint, Double> valueExtractor;

    MeasuredValue(String nameWithUnit, Function<ClimateDataPoint, Double> valueExtractor) {
        this.nameWithUnit = nameWithUnit;
        this.valueExtractor = valueExtractor;
    }
}
