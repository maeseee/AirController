package org.airController.controllers;

import org.airController.systemAdapter.ControlledVentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class AirController implements Runnable {
    private static final Logger logger = LogManager.getLogger(AirController.class);

    private final ControlledVentilationSystem ventilationSystem;
    private final DailyFreshAir dailyFreshAir;
    private final HourlyFreshAir hourlyFreshAir;
    private final HumidityExchanger humidityExchanger;
    private final AirQualityChecker airQualityChecker;
    private final SensorValues sensorValues;

    public AirController(ControlledVentilationSystem ventilationSystem, SensorValues sensorValues) {
        this(ventilationSystem, sensorValues, new DailyFreshAir(), new HourlyFreshAir(), new HumidityExchanger(), new AirQualityChecker());
    }

    AirController(ControlledVentilationSystem ventilationSystem, SensorValues sensorValues, DailyFreshAir dailyFreshAir,
                  HourlyFreshAir hourlyFreshAir, HumidityExchanger humidityExchanger, AirQualityChecker airQualityChecker) {
        this.ventilationSystem = ventilationSystem;
        this.sensorValues = sensorValues;
        this.dailyFreshAir = dailyFreshAir;
        this.hourlyFreshAir = hourlyFreshAir;
        this.humidityExchanger = humidityExchanger;
        this.airQualityChecker = airQualityChecker;
    }

    @Override
    public void run() {
        final LocalDateTime now = LocalDateTime.now();
        sensorValues.invalidateSensorValuesIfNeeded();
        final boolean freshAirOnForHumidityControl = humidityExchanger.turnFreshAirOn(sensorValues);
        final boolean freshAirOnForAirQualityControl = airQualityChecker.turnFreshAirOn(sensorValues);
        final boolean freshAirOnForDailyExchange = dailyFreshAir.turnFreshAirOn(now);
        final boolean freshAirOnForHourlyExchange = hourlyFreshAir.turnFreshAirOn(now.toLocalTime());
        final boolean freshAirOn =
                freshAirOnForHumidityControl || freshAirOnForDailyExchange || freshAirOnForHourlyExchange || freshAirOnForAirQualityControl;
        final boolean humidityExchangerOn = humidityExchanger.turnHumidityExchangerOn(sensorValues);
        final boolean stateChanged = ventilationSystem.setAirFlowOn(freshAirOn);
        ventilationSystem.setHumidityExchangerOn(humidityExchangerOn && freshAirOn);

        if (stateChanged && ventilationSystem.isAirFlowOn()) {
            final String freshAirRules = (freshAirOnForHumidityControl ? "HumidityControl " : "") +
                    (freshAirOnForAirQualityControl ? "AirQualityControl " : "") +
                    (freshAirOnForDailyExchange ? "DailyExchange " : "") +
                    (freshAirOnForHourlyExchange ? "HourlyExchange" : "");
            logger.info("Fresh air is on because of " + freshAirRules);
        }
    }
}
