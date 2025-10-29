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
import java.util.concurrent.Executors;

@Setter
class ApplicationBuilder {
    private final ApplicationBuilderSharedObjects sharedObjects;
    private final Sensors sensors;
    private final DailyOnTimeLogger statistics;
    private final RuleApplier ruleApplier;
    private final SystemStateLogger systemStateLogger;

    public ApplicationBuilder(ApplicationBuilderSharedObjects sharedObjects) {
        this.sharedObjects = sharedObjects;
        this.sensors = createSensors();
        this.statistics = createStatistics();
        this.ruleApplier = createRuleApplier();
        this.systemStateLogger = createSystemStateLogger();
    }

    public Application build() {
        validateParameters();
        return new Application(sensors, ruleApplier, statistics, systemStateLogger, Executors.newScheduledThreadPool(1));
    }

    private static Sensors createSensors() {
        return new SensorsBuilder().build();
    }

    private DailyOnTimeLogger createStatistics() {
        return new DailyOnTimeLogger(sharedObjects.getSystemActionDbAccessors().airFlow());
    }

    private RuleApplier createRuleApplier() {
        final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors);
        final List<Rule> humidityExchangeRules =
                new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sharedObjects.getCurrentSensors(sensors));
        return new RuleApplier(sharedObjects.getVentilationSystems(), freshAirRules, humidityExchangeRules);
    }

    private SystemStateLogger createSystemStateLogger() {
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
