package org.air_controller.rules;

import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.system_action.SystemActionDbAccessor;

import java.util.List;

public class FreshAirRuleBuilder {

    public List<Rule> build(ClimateSensors sensors, SystemActionDbAccessor dbAccessor) {
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(sensors.indoor());
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensors);
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(dbAccessor);
        return List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
    }
}
