package org.air_controller.rules;

import org.air_controller.sensor.Sensors;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.system.VentilationSystem;

import java.util.List;

public class RuleApplierBuilder {

    public RuleApplier build(List<VentilationSystem> ventilationSystems, Sensors sensors, TimeKeeper timeKeeper) {
        final List<Rule> freshAirRules = getFreshAirRules(sensors, timeKeeper);
        final List<Rule> exchangeHumidityRules = getHumidityExchangeRules(sensors);
        return new RuleApplier(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }

    private List<Rule> getFreshAirRules(Sensors sensors, TimeKeeper timeKeeper) {
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(currentIndoorSensorData);
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(timeKeeper);
        return List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
    }

    private List<Rule> getHumidityExchangeRules(Sensors sensors) {
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(currentIndoorSensorData, currentOutdoorSensorData);
        return List.of(humidityControlExchanger);
    }
}
