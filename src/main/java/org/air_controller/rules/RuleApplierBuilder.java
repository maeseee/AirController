package org.air_controller.rules;

import org.air_controller.sensor.Sensors;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system.VentilationSystemTimeKeeper;
import org.air_controller.system_action.SystemActionPersistence;

import java.util.List;

public class RuleApplierBuilder {

    public RuleApplier build(VentilationSystem ventilationSystem, Sensors sensors, VentilationSystemTimeKeeper timeKeeper,
            SystemActionPersistence systemActionPersistence) { // TODO add builders
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final List<VentilationSystem> ventilationSystems = List.of(ventilationSystem, timeKeeper, systemActionPersistence);
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
