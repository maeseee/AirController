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
    private final HumidityFreshAirRule humidityFreshAirRule;
    private final HumidityExchangerControlRule humidityExchangerControlRule;

    private AirValue indoorAirValue;
    private AirValue outdoorAirValue;

    public AirController(ControlledVentilationSystem controlledVentilationSystem) {
        this(controlledVentilationSystem, new DailyFreshAirRule(), new HourlyFreshAirRule(), new HumidityFreshAirRule(),
                new HumidityExchangerControlRule(), null);
    }

    AirController(ControlledVentilationSystem controlledVentilationSystem, DailyFreshAirRule dailyFreshAirRule, HourlyFreshAirRule hourlyFreshAirRule,
                  HumidityFreshAirRule humidityFreshAirRule, HumidityExchangerControlRule humidityExchangerControlRule, AirValue airValue) {
        this.controlledVentilationSystem = controlledVentilationSystem;
        this.dailyFreshAirRule = dailyFreshAirRule;
        this.hourlyFreshAirRule = hourlyFreshAirRule;
        this.humidityFreshAirRule = humidityFreshAirRule;
        this.humidityExchangerControlRule = humidityExchangerControlRule;
        this.indoorAirValue = airValue;
        this.outdoorAirValue = airValue;
    }

    public void runOneLoop() {
        final LocalDateTime now = LocalDateTime.now();
        final boolean sensorValuesAvailable = areSensorValuesAvailable();
        final boolean freshAirOnForHumidityControl = sensorValuesAvailable && humidityFreshAirRule.turnFreshAirOn(indoorAirValue, outdoorAirValue);
        final boolean freshAirOnForDailyExchange = dailyFreshAirRule.turnFreshAirOn(now);
        final boolean freshAirOnForHourlyExchange = hourlyFreshAirRule.turnFreshAirOn(now.toLocalTime());
        final boolean freshAirOn = freshAirOnForHumidityControl || freshAirOnForDailyExchange || freshAirOnForHourlyExchange;
        final boolean humidityExchangerOn =
                sensorValuesAvailable && humidityExchangerControlRule.turnHumidityExchangerOn(indoorAirValue, outdoorAirValue);
        controlledVentilationSystem.setAirFlowOn(freshAirOn);
        controlledVentilationSystem.setHumidityExchangerOn(humidityExchangerOn);
    }

    @Override
    public void updateIndoorAirValue(AirValue indoorAirValue) {
        this.indoorAirValue = indoorAirValue;
        runOneLoop();
    }

    @Override
    public void updateOutdoorAirValue(AirValue outdoorAirValue) {
        this.outdoorAirValue = outdoorAirValue;
        runOneLoop();
    }

    private boolean areSensorValuesAvailable() {
        return indoorAirValue != null && outdoorAirValue != null;
    }


}
