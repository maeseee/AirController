package org.airController.controllers;

import org.airController.sensorAdapter.*;
import org.airController.systemAdapter.ControlledVentilationSystem;

import java.time.LocalDateTime;

public class AirController implements IndoorSensorObserver, OutdoorSensorObserver {

    private final ControlledVentilationSystem controlledVentilationSystem;
    private final MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule;
    private final HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule;
    private final HumidityControlRule humidityControlRule;

    private SensorValue indoorSensorValue;
    private SensorValue outdoorSensorValue;

    public AirController(ControlledVentilationSystem controlledVentilationSystem) {
        this(controlledVentilationSystem, new MainFreshAirTimeSlotRule(), new HourlyFreshAirTimeSlotRule(), new HumidityControlRule());
    }

    AirController(ControlledVentilationSystem controlledVentilationSystem, MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule,
                  HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule, HumidityControlRule humidityControlRule) {
        this.controlledVentilationSystem = controlledVentilationSystem;
        this.mainFreshAirTimeSlotRule = mainFreshAirTimeSlotRule;
        this.hourlyFreshAirTimeSlotRule = hourlyFreshAirTimeSlotRule;
        this.humidityControlRule = humidityControlRule;
    }

    public void runOneLoop() {
        final LocalDateTime now = LocalDateTime.now();
        final boolean freshAirOn = mainFreshAirTimeSlotRule.turnFreshAirOn(now) || hourlyFreshAirTimeSlotRule.turnFreshAirOn(now.toLocalTime());
        final boolean humidityExchangerOn = humidityControlRule.turnHumidityExchangerOn(indoorSensorValue, outdoorSensorValue);
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
