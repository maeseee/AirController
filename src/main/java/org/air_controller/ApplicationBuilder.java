package org.air_controller;

import lombok.Setter;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
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

    ApplicationBuilder(ApplicationBuilderSharedObjects sharedObjects) {
        this.sharedObjects = sharedObjects;
        this.sensors = createSensors();
        this.statistics = createStatistics();
        this.ruleApplier = createRuleApplier();
        this.systemStateLogger = createSystemStateLogger();
    }

    public Application build() {
        return new Application(sensors, ruleApplier, statistics, systemStateLogger, Executors.newScheduledThreadPool(1));
    }

    public static ApplicationBuilder createBuilder() {
        final GpioPins gpios = createDingtianPins();
        final ApplicationBuilderSharedObjects sharedObjects = new ApplicationBuilderSharedObjects(gpios);
        return new ApplicationBuilder(sharedObjects);
    }

    private static Sensors createSensors() {
        return new SensorsBuilder().build();
    }

    private DailyOnTimeLogger createStatistics() {
        return new DailyOnTimeLogger(sharedObjects.getSystemActionDbAccessors().airFlow());
    }

    private RuleApplier createRuleApplier() {
        final List<Rule> freshAirRules = sharedObjects.getOrCreateFreshAirRules(sensors);
        final List<Rule> humidityExchangeRules =
                new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sharedObjects.getOrCreateCurrentSensors(sensors));
        return new RuleApplier(sharedObjects.getVentilationSystems(), freshAirRules, humidityExchangeRules);
    }

    private SystemStateLogger createSystemStateLogger() {
        final List<Rule> freshAirRules = sharedObjects.getOrCreateFreshAirRules(sensors);
        return new SystemStateLogger(sharedObjects.getVentilationSystems().getFirst(), freshAirRules);
    }

    private static GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }
}
