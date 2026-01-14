package org.air_controller;

import lombok.Setter;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.rules.FreshAirRuleBuilder;
import org.air_controller.rules.HumidityExchangerRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.ClimateSensorsFactory;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.statistics.DailyOnTimeLogger;
import org.air_controller.statistics.SystemStateLogger;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionPersistence;

import java.util.List;
import java.util.concurrent.Executors;

@Setter
class ApplicationBuilder {
    private final ApplicationPersistence persistence;
    private final GpioPins gpios;
    private final ClimateSensors sensors;
    private final List<VentilationSystem> ventilationSystems;
    private final List<Rule> freshAirRules;
    private final DailyOnTimeLogger statistics;
    private final RuleApplier ruleApplier;
    private final SystemStateLogger systemStateLogger;

    ApplicationBuilder() {
        this(createDingtianPins(), new ApplicationPersistence());
    }

    ApplicationBuilder(GpioPins gpioPins, ApplicationPersistence persistence) {
        this.persistence = persistence;
        this.gpios = gpioPins;
        this.sensors = createSensors();
        this.ventilationSystems = createVentilationSystems();
        this.freshAirRules = createFreshAirRules();
        this.statistics = createStatistics();
        this.ruleApplier = createRuleApplier();
        this.systemStateLogger = createSystemStateLogger();
    }

    public Application build() {
        return new Application(sensors, ruleApplier, statistics, systemStateLogger, Executors.newScheduledThreadPool(1));
    }

    private ClimateSensors createSensors() {
        return new ClimateSensorsFactory().build(persistence);
    }

    private DailyOnTimeLogger createStatistics() {
        return new DailyOnTimeLogger(persistence.getSystemActionDbAccessors().airFlow());
    }

    private List<VentilationSystem> createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(gpios);
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(persistence.getSystemActionDbAccessors());
        return List.of(ventilationSystem, systemActionPersistence);
    }

    private RuleApplier createRuleApplier() {
        final List<Rule> humidityExchangeRules =
                new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sensors);
        return new RuleApplier(ventilationSystems, freshAirRules, humidityExchangeRules);
    }

    private SystemStateLogger createSystemStateLogger() {
        return new SystemStateLogger(ventilationSystems.getFirst(), freshAirRules);
    }

    private static GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private List<Rule> createFreshAirRules() {
        return new FreshAirRuleBuilder().build(sensors, persistence.getSystemActionDbAccessors().airFlow());
    }
}
