package org.airController.controllers;

import org.airController.sensorAdapter.*;
import org.airController.systemAdapter.ControlledVentilationSystem;

import java.time.LocalDateTime;

public class AirController implements IndoorSensorObserver, OutdoorSensorObserver {

    private final ControlledVentilationSystem controlledVentilationSystem;
    private final DailyFreshAirRule dailyFreshAirRule;
    private final HourlyFreshAirRule hourlyFreshAirRule;
    private final HumidityExchangerControlRule humidityExchangerControlRule;

    private SensorValue indoorSensorValue;
    private SensorValue outdoorSensorValue;

    public AirController(ControlledVentilationSystem controlledVentilationSystem) {
        this(controlledVentilationSystem, new DailyFreshAirRule(), new HourlyFreshAirRule(), new HumidityExchangerControlRule());
    }

    AirController(ControlledVentilationSystem controlledVentilationSystem, DailyFreshAirRule dailyFreshAirRule,
                  HourlyFreshAirRule hourlyFreshAirRule, HumidityExchangerControlRule humidityExchangerControlRule) {
        this.controlledVentilationSystem = controlledVentilationSystem;
        this.dailyFreshAirRule = dailyFreshAirRule;
        this.hourlyFreshAirRule = hourlyFreshAirRule;
        this.humidityExchangerControlRule = humidityExchangerControlRule;
    }

    public void runOneLoop() {
        final LocalDateTime now = LocalDateTime.now();
        final boolean freshAirOn = dailyFreshAirRule.turnFreshAirOn(now) || hourlyFreshAirRule.turnFreshAirOn(now.toLocalTime());
        final boolean humidityExchangerOn = humidityExchangerControlRule.turnHumidityExchangerOn(indoorSensorValue, outdoorSensorValue);
        controlledVentilationSystem.setAirFlowOn(freshAirOn || humidityExchangerOn);
        controlledVentilationSystem.setHumidityExchangerOn(humidityExchangerOn);
    }

    @Override
    public void updateIndoorSensorValue(SensorValue indoorSensorValue) {
        this.indoorSensorValue = indoorSensorValue;
        runOneLoop();
    }

    @Override
    public void updateOutdoorSensorValue(SensorValue outdoorSensorValue) {
        this.outdoorSensorValue = outdoorSensorValue;
        runOneLoop();
    }
}
