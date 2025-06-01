package org.air_controller.rules;

import org.air_controller.sensor.Sensors;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system.VentilationSystemTimeKeeper;

import java.util.List;

public class RuleApplierBuilder {

    public RuleApplier build(List<VentilationSystem> ventilationSystems, Sensors sensors, VentilationSystemTimeKeeper timeKeeper) {
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(currentIndoorSensorData);
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(timeKeeper);
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(humidityControlAirFlow);

        final List<Rule> freshAirRules = List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
        final List<Rule> exchangeHumidityRules = List.of(humidityControlExchanger);
        return new RuleApplier(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }
}
