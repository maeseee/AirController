package org.controllers;

import org.outputsystem.ControlledVentilationSystem;
import org.sensors.IndoorAirValues;
import org.sensors.OutdoorAirValues;

import java.time.LocalDateTime;

public class AirController {

    private final IndoorAirValues indoorAirValues;
    private final OutdoorAirValues outdoorAirValues;
    private final ControlledVentilationSystem controlledVentilationSystem;
    private final MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule;
    private final HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule;
    private final HumidityControlRule humidityControlRule;


    public AirController(IndoorAirValues indoorAirValues, OutdoorAirValues outdoorAirValues,
                         ControlledVentilationSystem controlledVentilationSystem) {
        this(indoorAirValues, outdoorAirValues, controlledVentilationSystem, new MainFreshAirTimeSlotRule(), new HourlyFreshAirTimeSlotRule(),
                new HumidityControlRule());
    }

    AirController(IndoorAirValues indoorAirValues, OutdoorAirValues outdoorAirValues, ControlledVentilationSystem controlledVentilationSystem,
                  MainFreshAirTimeSlotRule mainFreshAirTimeSlotRule, HourlyFreshAirTimeSlotRule hourlyFreshAirTimeSlotRule,
                  HumidityControlRule humidityControlRule) {
        this.indoorAirValues = indoorAirValues;
        this.outdoorAirValues = outdoorAirValues;
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
}