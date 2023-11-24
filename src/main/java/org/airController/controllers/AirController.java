package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.airController.systemAdapter.ControlledVentilationSystem;

import java.time.LocalDateTime;

public class AirController implements IndoorSensorObserver, OutdoorSensorObserver {

    private final ControlledVentilationSystem controlledVentilationSystem;
    private final DailyFreshAirRule dailyFreshAirRule;
    private final HourlyFreshAirRule hourlyFreshAirRule;
    private final HumidityExchangerControlRule humidityExchangerControlRule;

    private AirValue indoorAirValue;
    private AirValue outdoorAirValue;

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
        final boolean humidityExchangerOn = humidityExchangerControlRule.turnHumidityExchangerOn(indoorAirValue, outdoorAirValue);
        final boolean canHumidityBeOptimized = !humidityExchangerOn;
        controlledVentilationSystem.setAirFlowOn(freshAirOn || canHumidityBeOptimized);
        controlledVentilationSystem.setHumidityExchangerOn(humidityExchangerOn);
    }

    @Override
    public void updateIndoorSensorValue(AirValue indoorAirValue) {
        this.indoorAirValue = indoorAirValue;
        runOneLoop();
    }

    @Override
    public void updateOutdoorSensorValue(AirValue outdoorAirValue) {
        this.outdoorAirValue = outdoorAirValue;
        runOneLoop();
    }
}
