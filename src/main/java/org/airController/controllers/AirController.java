package org.airController.controllers;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.sensorAdapter.OutdoorSensorObserver;
import org.airController.systemAdapter.ControlledVentilationSystem;

import java.time.LocalDateTime;

public class AirController implements IndoorSensorObserver, OutdoorSensorObserver {

    private final ControlledVentilationSystem ventilationSystem;
    private final DailyFreshAir dailyFreshAir;
    private final HourlyFreshAir hourlyFreshAir;
    private final HumidityFreshAir humidityFreshAir;
    private final HumidityExchanger humidityExchanger;

    private AirValue indoorAirValue;
    private AirValue outdoorAirValue;

    public AirController(ControlledVentilationSystem ventilationSystem) {
        this(ventilationSystem, new DailyFreshAir(), new HourlyFreshAir(), new HumidityFreshAir(), new HumidityExchanger());
    }

    AirController(ControlledVentilationSystem ventilationSystem, DailyFreshAir dailyFreshAir, HourlyFreshAir hourlyFreshAir,
                  HumidityFreshAir humidityFreshAir, HumidityExchanger humidityExchanger) {
        this.ventilationSystem = ventilationSystem;
        this.dailyFreshAir = dailyFreshAir;
        this.hourlyFreshAir = hourlyFreshAir;
        this.humidityFreshAir = humidityFreshAir;
        this.humidityExchanger = humidityExchanger;
    }

    @Override
    public void runOneLoop() {
        final LocalDateTime now = LocalDateTime.now();
        final boolean sensorValuesAvailable = areSensorValuesAvailable();
        final boolean freshAirOnForHumidityControl = sensorValuesAvailable && humidityFreshAir.turnFreshAirOn(indoorAirValue, outdoorAirValue);
        final boolean freshAirOnForDailyExchange = dailyFreshAir.turnFreshAirOn(now);
        final boolean freshAirOnForHourlyExchange = hourlyFreshAir.turnFreshAirOn(now.toLocalTime());
        final boolean freshAirOn = freshAirOnForHumidityControl || freshAirOnForDailyExchange || freshAirOnForHourlyExchange;
        final boolean humidityExchangerOn = sensorValuesAvailable && humidityExchanger.turnHumidityExchangerOn(indoorAirValue, outdoorAirValue);
        ventilationSystem.setAirFlowOn(freshAirOn);
        ventilationSystem.setHumidityExchangerOn(humidityExchangerOn && freshAirOn);
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
