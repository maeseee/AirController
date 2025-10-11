package org.air_controller;

import lombok.Setter;
import org.air_controller.rules.HumidityExchangerRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.Sensors;
import org.air_controller.sensor.SensorsBuilder;
import org.air_controller.statistics.DailyOnTimeLogger;
import org.air_controller.statistics.SystemStateLogger;

import java.util.List;

@Setter
class ApplicationBuilder {
    private Sensors sensors;
    private RuleApplier ruleApplier;
    private DailyOnTimeLogger statistics;
    private SystemStateLogger systemStateLogger;
    private ApplicationBuilderSharedObjects sharedObjects = new ApplicationBuilderSharedObjects();

    public Application build() {
        validateParameters();
        return new Application(sensors, ruleApplier, statistics, systemStateLogger);
    }

    public Sensors createSensors() {
        return new SensorsBuilder().build();
    }

    public DailyOnTimeLogger createStatistics() {
        return new DailyOnTimeLogger(sharedObjects.getSystemActionDbAccessors().airFlow());
    }

    public RuleApplier createRuleApplier() {
        final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors);
        final List<Rule> humidityExchangeRules =
                new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sharedObjects.getCurrentSensors(sensors));
        return new RuleApplier(sharedObjects.getVentilationSystems(), freshAirRules, humidityExchangeRules);
    }

    public SystemStateLogger createSystemStateLogger() {
        final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors);
        return new SystemStateLogger(sharedObjects.getVentilationSystems().getFirst(), freshAirRules);
    }

    private void validateParameters() {
        if (sensors == null) {
            throw new IllegalStateException("sensors is null");
        }
        if (ruleApplier == null) {
            throw new IllegalStateException("ruleApplier is null");
        }
        if (statistics == null) {
            throw new IllegalStateException("statistics is null");
        }
        if (systemStateLogger == null) {
            throw new IllegalStateException("systemStateLogger is null");
        }
    }
}
