package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class AirController implements IndoorSensorObserver, OutdoorSensorObserver, Runnable {
    private static final Logger logger = LogManager.getLogger(AirController.class);

    private final ControlledVentilationSystem ventilationSystem;
    private final DailyFreshAir dailyFreshAir;
    private final HourlyFreshAir hourlyFreshAir;
    private final HumidityExchanger humidityExchanger;

    private AirValue indoorAirValue;
    private AirValue outdoorAirValue;

    public AirController(ControlledVentilationSystem ventilationSystem) {
        this(ventilationSystem, new DailyFreshAir(), new HourlyFreshAir(), new HumidityExchanger());
    }

    AirController(ControlledVentilationSystem ventilationSystem, DailyFreshAir dailyFreshAir, HourlyFreshAir hourlyFreshAir,
                  HumidityExchanger humidityExchanger) {
        this.ventilationSystem = ventilationSystem;
        this.dailyFreshAir = dailyFreshAir;
        this.hourlyFreshAir = hourlyFreshAir;
        this.humidityExchanger = humidityExchanger;
    }

    @Override
    public void run() {
        final LocalDateTime now = LocalDateTime.now();
        final boolean sensorValuesAvailable = areSensorValuesAvailable();
        final boolean freshAirOnForHumidityControl = sensorValuesAvailable && humidityExchanger.turnFreshAirOn(indoorAirValue, outdoorAirValue);
        final boolean freshAirOnForDailyExchange = dailyFreshAir.turnFreshAirOn(now);
        final boolean freshAirOnForHourlyExchange = hourlyFreshAir.turnFreshAirOn(now.toLocalTime());
        final boolean freshAirOn = freshAirOnForHumidityControl || freshAirOnForDailyExchange || freshAirOnForHourlyExchange;
        final boolean humidityExchangerOn = sensorValuesAvailable && humidityExchanger.turnHumidityExchangerOn(indoorAirValue, outdoorAirValue);
        final boolean stateChanged = ventilationSystem.setAirFlowOn(freshAirOn);
        ventilationSystem.setHumidityExchangerOn(humidityExchangerOn && freshAirOn);

        if (stateChanged && ventilationSystem.isAirFlowOn()) {
            final String freshAirRules = (freshAirOnForHumidityControl ? "HumidityControl " : "") +
                    (freshAirOnForDailyExchange ? "DailyExchange " : "") +
                    (freshAirOnForHourlyExchange ? "HourlyExchange" : "");
            logger.info("Fresh air is on because of " + freshAirRules);
        }
    }

    @Override
    public void updateIndoorAirValue(AirValue indoorAirValue) {
        this.indoorAirValue = indoorAirValue;
    }

    @Override
    public void updateOutdoorAirValue(AirValue outdoorAirValue) {
        this.outdoorAirValue = outdoorAirValue;
    }

    private boolean areSensorValuesAvailable() {
        return indoorAirValue != null && outdoorAirValue != null;
    }

}
