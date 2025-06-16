package org.air_controller;

import lombok.Setter;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.persistence.Persistence;
import org.air_controller.rules.RuleApplier;
import org.air_controller.rules.RuleApplierBuilder;
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

    private Sensors sensors;
    private RuleApplier ruleApplier;
    private SystemStatistics statistics;
    private ScheduledExecutorService executor;

    private SystemActionDbAccessors systemActionDbAccessors;
    private GpioPins gpioPins;

    public Application build() throws SQLException {
        createNotMockedObjects();
        return new Application(sensors, ruleApplier, statistics, executor);
    }

    private void createNotMockedObjects() throws SQLException {
        createSensorsIfNotAvailable();
        createSystemActionDbAccessorsIfNotAvailable();
        createTimeKeeperIfNotAvailable();
        createGpioPinsIfNotAvailable();
        createRuleApplierIfNotAvailable();
        createExecutorIfNotAvailable();
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
            ruleApplier = new RuleApplierBuilder().build(createVentilationSystems(), createCurrentSensors(), statistics);
        }
    }

    private void createExecutorIfNotAvailable() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(1);
        }
    }

    private GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private SystemActionDbAccessors createSystemActionDbAccessors() throws SQLException {
        return new SystemActionDbAccessors(createDbAccessor(SystemPart.AIR_FLOW), createDbAccessor(SystemPart.HUMIDITY));
    }

    private SystemActionDbAccessor createDbAccessor(SystemPart systemPart) throws SQLException {
        return new SystemActionDbAccessor(new Persistence(), systemPart);
    }

    private List<VentilationSystem> createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(gpioPins);
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(systemActionDbAccessors);
        return List.of(ventilationSystem, systemActionPersistence);
    }

    private CurrentSensors createCurrentSensors() {
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        return new CurrentSensors(currentIndoorSensorData, currentOutdoorSensorData);
    }
}
