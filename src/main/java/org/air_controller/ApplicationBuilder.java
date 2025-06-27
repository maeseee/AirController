package org.air_controller;

import lombok.Setter;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.rules.FreshAirRuleBuilder;
import org.air_controller.rules.HumidityExchangerRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.Sensors;
import org.air_controller.sensor.SensorsBuilder;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.CurrentSensors;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.SystemStatistics;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemActionPersistence;
import org.air_controller.system_action.SystemPart;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Setter
class ApplicationBuilder {

    // necessary objects
    private Sensors sensors;
    private RuleApplier ruleApplier;
    private SystemStatistics statistics;
    private ScheduledExecutorService executor;
    private SystemStateLogger systemStateLogger;

    // shared helper objects
    private SystemActionDbAccessors systemActionDbAccessors;
    private GpioPins gpioPins;
    private List<VentilationSystem> ventilationSystems;
    private CurrentSensors currentSensors;
    private List<Rule> freshAirRules;

    public Application build() throws SQLException {
        createNotMockedObjects();
        return new Application(sensors, ruleApplier, statistics, executor, systemStateLogger);
    }

    private void createNotMockedObjects() throws SQLException {
        createSensorsIfNotAvailable();
        createSystemActionDbAccessorsIfNotAvailable();
        createTimeKeeperIfNotAvailable();
        createGpioPinsIfNotAvailable();
        createRuleApplierIfNotAvailable();
        createExecutorIfNotAvailable();
        createSystemStateLoggerIfNotAvailable();
    }

    private void createSensorsIfNotAvailable() {
        if (sensors == null) {
            sensors = new SensorsBuilder().build();
        }
    }

    private void createSystemActionDbAccessorsIfNotAvailable() throws SQLException {
        if (systemActionDbAccessors == null) {
            systemActionDbAccessors = createSystemActionDbAccessors();
        }
    }

    private void createTimeKeeperIfNotAvailable() {
        if (statistics == null) {
            statistics = new SystemStatistics(systemActionDbAccessors.airFlow());
        }
    }

    private void createGpioPinsIfNotAvailable() {
        if (gpioPins == null) {
            gpioPins = createDingtianPins();
        }
    }

    private void createRuleApplierIfNotAvailable() {
        if (ruleApplier == null) {
            final List<Rule> freshAirRules = getFreshAirRules();
            final List<Rule> humidityExchangeRules = new HumidityExchangerRuleBuilder().getHumidityExchangeRules(getCurrentSensors());
            ruleApplier = new RuleApplier(getVentilationSystems(), freshAirRules, humidityExchangeRules);
        }
    }

    private void createExecutorIfNotAvailable() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(1);
        }
    }

    private void createSystemStateLoggerIfNotAvailable() {
        if (systemStateLogger == null) {
            systemStateLogger = new SystemStateLogger(getVentilationSystems().getFirst(), getFreshAirRules());
        }
    }

    private GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private SystemActionDbAccessors createSystemActionDbAccessors() throws SQLException {
        return new SystemActionDbAccessors(createDbAccessor(SystemPart.AIR_FLOW), createDbAccessor(SystemPart.HUMIDITY));
    }

    private SystemActionDbAccessor createDbAccessor(SystemPart systemPart) throws SQLException {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }

    private List<VentilationSystem> getVentilationSystems() {
        if (ventilationSystems == null) {
            createVentilationSystems();
        }
        return ventilationSystems;
    }

    private void createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(gpioPins);
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(systemActionDbAccessors);
        ventilationSystems = List.of(ventilationSystem, systemActionPersistence);
    }

    private CurrentSensors getCurrentSensors() {
        if (currentSensors == null) {
            createCurrentSensors();
        }
        return currentSensors;
    }

    private void createCurrentSensors() {
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        currentSensors = new CurrentSensors(currentIndoorSensorData, currentOutdoorSensorData);
    }

    private List<Rule> getFreshAirRules() {
        if (freshAirRules == null) {
            freshAirRules = new FreshAirRuleBuilder().build(getCurrentSensors(), statistics);
        }
        return freshAirRules;
    }

}
