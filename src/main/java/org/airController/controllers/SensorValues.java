package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class SensorValues implements IndoorSensorObserver, OutdoorSensorObserver {
    private static final Logger logger = LogManager.getLogger(SensorValues.class);
    private static final int SENSOR_INVALIDATION_DURATION = 4;

    private AirValue indoorAirValue;
    private LocalDateTime lastIndoorAirValueUpdate;
    private AirValue outdoorAirValue;
    private LocalDateTime lastOutdoorAirValueUpdate;

    public SensorValues() {
    }

    SensorValues(AirValue indoorAirValue, AirValue outdoorAirValue) {
        this.indoorAirValue = indoorAirValue;
        this.outdoorAirValue = outdoorAirValue;
    }

    public double getIndoorAbsoluteHumidity() {
        return indoorAirValue.getAbsoluteHumidity();
    }

    public double getOutdoorAbsoluteHumidity() {
        return outdoorAirValue.getAbsoluteHumidity();
    }

    public Temperature getIndoorTemperature() {
        return indoorAirValue.getTemperature();
    }

    public Temperature getOutdoorTemperature() {
        return outdoorAirValue.getTemperature();
    }

    public boolean isASensorValueMissing() {
        return indoorAirValue == null || outdoorAirValue == null;
    }

    public void invalidateSensorValuesIfNeeded(LocalDateTime now) {
        if (lastIndoorAirValueUpdate == null || lastOutdoorAirValueUpdate == null) {
            logger.info("There was no sensor update jet!");
            return;
        }
        final LocalDateTime invalidationTime = now.minusHours(SENSOR_INVALIDATION_DURATION);
        if (invalidationTime.isAfter(lastIndoorAirValueUpdate)) {
            logger.error("There was no indoor sensor update for at least " + SENSOR_INVALIDATION_DURATION + " hours!");
            indoorAirValue = null;
        }

        if (invalidationTime.isAfter(lastOutdoorAirValueUpdate)) {
            logger.error("There was no outdoor sensor update for at least " + SENSOR_INVALIDATION_DURATION + " hours!");
            outdoorAirValue = null;
        }
    }

    @Override
    public void updateIndoorAirValue(AirValue indoorAirValue) {
        this.indoorAirValue = indoorAirValue;
        lastIndoorAirValueUpdate = LocalDateTime.now();
    }

    @Override
    public void updateOutdoorAirValue(AirValue outdoorAirValue) {
        this.outdoorAirValue = outdoorAirValue;
        lastOutdoorAirValueUpdate = LocalDateTime.now();
    }
}
