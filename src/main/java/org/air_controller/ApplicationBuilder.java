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
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.VentilationSystemPersistence;

import java.util.List;
import java.util.concurrent.Executors;

@Setter
class ApplicationBuilder {
    private final ApplicationPersistence persistence;
    private final GpioPins gpios;
    private final ClimateSensors sensors;
    private final VentilationSystem ventilationSystem;
    private final VentilationSystemPersistence ventilationSystemPersistence;
    private final List<Rule> freshAirRules;
    private final DailyOnTimeLogger statistics;
    private final RuleApplier ruleApplier;

    ApplicationBuilder() {
        this(createDingtianPins(), new ApplicationPersistence());
    }

    ApplicationBuilder(GpioPins gpioPins, ApplicationPersistence persistence) {
        this.persistence = persistence;
        this.gpios = gpioPins;
        this.sensors = createSensors();
        this.ventilationSystem = createVentilationSystem();
        this.ventilationSystemPersistence = createVentilationSystemPersistence();
        this.freshAirRules = createFreshAirRules();
        this.statistics = createStatistics();
        this.ruleApplier = createRuleApplier();
    }

    public Application build() {
        return new Application(sensors, ruleApplier, statistics, Executors.newScheduledThreadPool(1));
    }

    private ClimateSensors createSensors() {
        return new ClimateSensorsFactory().build(persistence);
    }

    private DailyOnTimeLogger createStatistics() {
        return new DailyOnTimeLogger(persistence.getVentilationSystemDbAccessors().airFlow());
    }

    private VentilationSystem createVentilationSystem() {
        return new ControlledVentilationSystem(gpios);
    }

    private VentilationSystemPersistence createVentilationSystemPersistence() {
        return new VentilationSystemPersistence(persistence.getVentilationSystemDbAccessors());
    }

    private RuleApplier createRuleApplier() {
        final List<Rule> humidityExchangeRules =
                new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sensors);
        return new RuleApplier(ventilationSystem, ventilationSystemPersistence, freshAirRules, humidityExchangeRules);
    }

    private static GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private List<Rule> createFreshAirRules() {
        return new FreshAirRuleBuilder().build(sensors, persistence.getVentilationSystemDbAccessors().airFlow());
    }
}
