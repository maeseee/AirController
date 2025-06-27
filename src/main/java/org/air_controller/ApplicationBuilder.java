package org.air_controller;

import lombok.Setter;
import org.air_controller.rules.HumidityExchangerRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.Sensors;
import org.air_controller.sensor.SensorsBuilder;
import org.air_controller.statistics.SystemStateLogger;
import org.air_controller.system.SystemStatistics;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Setter
class ApplicationBuilder {

    private Sensors sensors;
    private RuleApplier ruleApplier;
    private SystemStatistics statistics;
    private ScheduledExecutorService executor;
    private SystemStateLogger systemStateLogger;
    private ApplicationBuilderSharedObjects sharedObjects = new ApplicationBuilderSharedObjects();

    public Application build() throws SQLException {
        createNotMockedObjects();
        return new Application(sensors, ruleApplier, statistics, executor, systemStateLogger);
    }

    private void createNotMockedObjects() {
        createSensorsIfNotAvailable();
        createTimeKeeperIfNotAvailable();
        createRuleApplierIfNotAvailable();
        createExecutorIfNotAvailable();
        createSystemStateLoggerIfNotAvailable();
    }

    private void createSensorsIfNotAvailable() {
        if (sensors == null) {
            sensors = new SensorsBuilder().build();
        }
    }

    private void createTimeKeeperIfNotAvailable() {
        if (statistics == null) {
            statistics = new SystemStatistics(sharedObjects.getSystemActionDbAccessors().airFlow());
        }
    }

    private void createRuleApplierIfNotAvailable() {
        if (ruleApplier == null) {
            final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors, statistics);
            final List<Rule> humidityExchangeRules =
                    new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sharedObjects.getCurrentSensors(sensors));
            ruleApplier = new RuleApplier(sharedObjects.getVentilationSystems(), freshAirRules, humidityExchangeRules);
        }
    }

    private void createExecutorIfNotAvailable() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(1);
        }
    }

    private void createSystemStateLoggerIfNotAvailable() {
        if (systemStateLogger == null) {
            final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors, statistics);
            systemStateLogger = new SystemStateLogger(sharedObjects.getVentilationSystems().getFirst(), freshAirRules);
        }
    }
}
