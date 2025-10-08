package org.air_controller;

import lombok.Setter;
import org.air_controller.rules.HumidityExchangerRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.Sensors;
import org.air_controller.sensor.SensorsBuilder;
import org.air_controller.statistics.DailyOnTimeLogger;
import org.air_controller.statistics.SystemStateLogger;

import java.sql.SQLException;
import java.util.List;

@Setter
class ApplicationBuilder {

    private Sensors sensors;
    private RuleApplier ruleApplier;
    private DailyOnTimeLogger statistics;
    private SystemStateLogger systemStateLogger;
    private ApplicationBuilderSharedObjects sharedObjects = new ApplicationBuilderSharedObjects();

    public Application build() throws SQLException {
        createNotMockedObjects();
        return new Application(sensors, ruleApplier, statistics, systemStateLogger);
    }

    private void createNotMockedObjects() {
        createSensorsIfNotAvailable();
        createTimeKeeperIfNotAvailable();
        createRuleApplierIfNotAvailable();
        createSystemStateLoggerIfNotAvailable();
    }

    private void createSensorsIfNotAvailable() {
        if (sensors == null) {
            sensors = new SensorsBuilder().build();
        }
    }

    private void createTimeKeeperIfNotAvailable() {
        if (statistics == null) {
            statistics = new DailyOnTimeLogger(sharedObjects.getSystemActionDbAccessors().airFlow());
        }
    }

    private void createRuleApplierIfNotAvailable() {
        if (ruleApplier == null) {
            final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors);
            final List<Rule> humidityExchangeRules =
                    new HumidityExchangerRuleBuilder().getHumidityExchangeRules(sharedObjects.getCurrentSensors(sensors));
            ruleApplier = new RuleApplier(sharedObjects.getVentilationSystems(), freshAirRules, humidityExchangeRules);
        }
    }

    private void createSystemStateLoggerIfNotAvailable() {
        if (systemStateLogger == null) {
            final List<Rule> freshAirRules = sharedObjects.getFreshAirRules(sensors);
            systemStateLogger = new SystemStateLogger(sharedObjects.getVentilationSystems().getFirst(), freshAirRules);
        }
    }
}
