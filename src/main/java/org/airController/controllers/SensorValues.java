package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SensorValues implements IndoorSensorObserver, OutdoorSensorObserver {
    private static final Logger logger = LogManager.getLogger(SensorValues.class);
    private static final int SENSOR_INVALIDATION_DURATION = 4;

    private AirValue indoorAirValue;
    private AirValue outdoorAirValue;

    public SensorValues() {
    }

    SensorValues(AirValue indoorAirValue, AirValue outdoorAirValue) {
        this.indoorAirValue = indoorAirValue;
        this.outdoorAirValue = outdoorAirValue;
    }

    public void invalidateSensorValuesIfNeeded() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime invalidationTime = now.minusHours(SENSOR_INVALIDATION_DURATION);
        checkIndoorAirValue(now, invalidationTime);
        checkOutdoorAirValue(now, invalidationTime);
    }

    public boolean isIndoorHumidityAboveUpperTarget(Humidity upperTargetHumidity) {
        if (indoorAirValue == null) {
            return false;
        }
        final double upperTargetAbsoluteHumidity = upperTargetHumidity.getAbsoluteHumidity(indoorAirValue.getTemperature());
        return indoorAirValue.getAbsoluteHumidity() > upperTargetAbsoluteHumidity;
    }

    public boolean isIndoorHumidityBelowLowerTarget(Humidity lowerTargetHumidity) {
        if (indoorAirValue == null) {
            return false;
        }
        final double lowerTargetAbsoluteHumidity = lowerTargetHumidity.getAbsoluteHumidity(indoorAirValue.getTemperature());
        return indoorAirValue.getAbsoluteHumidity() < lowerTargetAbsoluteHumidity;
    }

    public boolean isOutdoorHumidityBelowLowerTarget(Humidity lowerTargetHumidity) {
        if (indoorAirValue == null || outdoorAirValue == null) {
            return false;
        }
        final double lowerTargetAbsoluteHumidity = lowerTargetHumidity.getAbsoluteHumidity(indoorAirValue.getTemperature());
        return outdoorAirValue.getAbsoluteHumidity() < lowerTargetAbsoluteHumidity;
    }

    public boolean isOutdoorHumidityAboveUpperTarget(Humidity upperTargetHumidity) {
        if (indoorAirValue == null || outdoorAirValue == null) {
            return false;
        }
        final double upperTargetAbsoluteHumidity = upperTargetHumidity.getAbsoluteHumidity(indoorAirValue.getTemperature());
        return outdoorAirValue.getAbsoluteHumidity() > upperTargetAbsoluteHumidity;
    }

    public boolean isIndoorHumidityAboveOutdoorHumidity() {
        if (indoorAirValue == null || outdoorAirValue == null) {
            return false;
        }
        return indoorAirValue.getAbsoluteHumidity() > outdoorAirValue.getAbsoluteHumidity();
    }

    public Humidity getIndoorHumidity() {
        return indoorAirValue.getHumidity();
    }

    @Override
    public void updateIndoorAirValue(AirValue indoorAirValue) {
        this.indoorAirValue = indoorAirValue;
    }

    @Override
    public void updateOutdoorAirValue(AirValue outdoorAirValue) {
        this.outdoorAirValue = outdoorAirValue;
    }

    public Optional<CarbonDioxide> getIndoorCo2() {
        if (indoorAirValue == null) {
            return Optional.empty();
        }
        return indoorAirValue.getCo2();
    }

    AirValue getIndoorAirValue() {
        return indoorAirValue;
    }

    AirValue getOutdoorAirValue() {
        return outdoorAirValue;
    }

    private void checkIndoorAirValue(LocalDateTime now, LocalDateTime invalidationTime) {
        if (indoorAirValue != null && invalidationTime.isAfter(indoorAirValue.getTimeStamp())) {
            final Duration lastUpdate = Duration.between(indoorAirValue.getTimeStamp(), now);
            logger.error("There was no indoor sensor update for " + lastUpdate.toHours() + " hours!");
            indoorAirValue = null;
        }
    }

    private void checkOutdoorAirValue(LocalDateTime now, LocalDateTime invalidationTime) {
        if (outdoorAirValue != null && invalidationTime.isAfter(outdoorAirValue.getTimeStamp())) {
            final Duration lastUpdate = Duration.between(outdoorAirValue.getTimeStamp(), now);
            logger.error("There was no outdoor sensor update for " + lastUpdate.toHours() + " hours!");
            outdoorAirValue = null;
        }
    }
}
