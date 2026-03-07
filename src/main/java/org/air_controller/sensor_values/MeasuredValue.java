package org.air_controller.sensor_values;

import lombok.Getter;
import org.air_controller.web_access.card_view.CardItem;

import java.util.function.Function;

@Getter
public enum MeasuredValue {
    TEMPERATURE("Temperature", "°C", dataPoint -> dataPoint.temperature().celsius()),
    HUMIDITY("Humidity", "%", dataPoint -> dataPoint.humidity().getRelativeHumidity(dataPoint.temperature())),
    CO2("CO2", "ppm", dataPoint -> dataPoint.co2().map(CarbonDioxide::ppm).orElse(null));

    private final String valueName;
    private final String unit;
    private final Function<ClimateDataPoint, Double> valueExtractor;

    MeasuredValue(String valueName, String unit, Function<ClimateDataPoint, Double> valueExtractor) {
        this.valueName = valueName;
        this.unit = unit;
        this.valueExtractor = valueExtractor;
    }

    public String nameWithUnit() {
        return valueName + " (" + unit + ")";
    }

    public CardItem toCardItem(String value) {
        return new CardItem(getValueName(), value, unit);
    }
}
