package org.airController.controllers;

import org.airController.entities.Humidity;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorAirMeasurementObserver;
import org.airController.sensorAdapter.IndoorAirValues;
import org.airController.sensorAdapter.OutdoorAirMeasurementObserver;
import org.airController.sensorAdapter.OutdoorAirValues;
import org.airController.systemAdapter.ControlledVentilationSystem;

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
        indoorAirValues = new IndoorAirValues(Temperature.createFromCelsius(23.0), Humidity.createFromRelative(50.0));
        outdoorAirValues = new OutdoorAirValues(Temperature.createFromCelsius(15), Humidity.createFromRelative(50.0));
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
        runOneLoop();
    }

    @Override
    public void updateAirMeasurement(OutdoorAirValues outdoorAirValues) {
        this.outdoorAirValues = outdoorAirValues;
        runOneLoop();
    }
}
