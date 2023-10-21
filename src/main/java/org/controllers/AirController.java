package org.controllers;

import org.outputsystem.ControlledVentilationSystem;
import org.sensors.IndoorAirMeasurementObserver;
import org.sensors.IndoorAirValues;
import org.sensors.OutdoorAirMeasurementObserver;
import org.sensors.OutdoorAirValues;

import java.time.LocalDateTime;

public class AirController implements IndoorAirMeasurementObserver, OutdoorAirMeasurementObserver {

    private final ControlledVentilationSystem controlledVentilationSystem;
    private final MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule;
    private final HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule;
    private final HumidityControlRule humidityControlRule;

    private IndoorAirValues indoorAirValues;
    private OutdoorAirValues outdoorAirValues;

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
        final boolean humidityExchangerOn = humidityControlRule.turnHumidityExchangerOn(indoorAirValues, outdoorAirValues);
        controlledVentilationSystem.setAirFlowOn(freshAirOn || humidityExchangerOn);
        controlledVentilationSystem.setHumidityExchangerOn(humidityExchangerOn);
    }

    @Override
    public void updateAirMeasurement(IndoorAirValues indoorAirValues) {
        this.indoorAirValues = indoorAirValues;
    }

    @Override
    public void updateAirMeasurement(OutdoorAirValues outdoorAirValues) {
        this.outdoorAirValues = outdoorAirValues;
    }
}
