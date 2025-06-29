package org.air_controller.rules;

import org.air_controller.sensor_values.CurrentSensors;
import org.air_controller.system_action.SystemActionDbAccessor;

import java.util.List;

public class FreshAirRuleBuilder {

    public List<Rule> build(CurrentSensors sensors, SystemActionDbAccessor dbAccessor) {
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(sensors.indoorData());
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensors.indoorData(), sensors.outdoorData());
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(dbAccessor);
        return List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
    }
}
