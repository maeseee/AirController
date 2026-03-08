package org.air_controller.sensor_values;

import lombok.Getter;
import org.air_controller.web_access.card_view.CardItem;

@Getter
public enum MeasuredValue {
    TEMPERATURE("Temperature", "°C"),
    HUMIDITY("Humidity", "%"),
    CO2("CO2", "ppm");

    private final String valueName;
    private final String unit;

    MeasuredValue(String valueName, String unit) {
        this.valueName = valueName;
        this.unit = unit;
    }

    public String nameWithUnit() {
        return valueName + " (" + unit + ")";
    }

    public CardItem toCardItem(String value) {
        return new CardItem(getValueName(), value, unit);
    }
}
