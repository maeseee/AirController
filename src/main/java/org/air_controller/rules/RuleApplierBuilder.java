package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensors;
import org.air_controller.system.VentilationSystem;

import java.util.List;

public class RuleApplierBuilder {

    public RuleApplier build(List<VentilationSystem> ventilationSystems, CurrentSensors sensors, AirFlowStatistics statistics) {
        final List<Rule> freshAirRules = getFreshAirRules(sensors, statistics);
        final List<Rule> exchangeHumidityRules = getHumidityExchangeRules(sensors);
        return new RuleApplier(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }

    private List<Rule> getFreshAirRules(CurrentSensors sensors, AirFlowStatistics statistics) {
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(sensors.indoorData());
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensors.indoorData(), sensors.outdoorData());
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(statistics);
        return List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
    }

    private List<Rule> getHumidityExchangeRules(CurrentSensors sensors) {
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(sensors.indoorData(), sensors.outdoorData());
        return List.of(humidityControlExchanger);
    }
}
