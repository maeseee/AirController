package org.air_controller;

import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.rules.FreshAirRuleBuilder;
import org.air_controller.rules.Rule;
import org.air_controller.sensor.Sensors;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.sensor_values.CurrentSensors;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemActionPersistence;
import org.air_controller.system_action.SystemPart;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

class ApplicationBuilderSharedObjects {
    private SystemActionDbAccessors systemActionDbAccessors;
    private List<VentilationSystem> ventilationSystems;
    private CurrentSensors currentSensors;
    private List<Rule> freshAirRules;
    private GpioPins gpioPins;

    public ApplicationBuilderSharedObjects() {
        this(createSystemActionDbAccessors(), createDingtianPins());
    }

    @VisibleForTesting
    public ApplicationBuilderSharedObjects(SystemActionDbAccessors systemActionDbAccessors, GpioPins gpioPins) {
        this.systemActionDbAccessors = systemActionDbAccessors;
        this.gpioPins = gpioPins;
    }


    public SystemActionDbAccessors getSystemActionDbAccessors() {
        if (systemActionDbAccessors == null) {
            systemActionDbAccessors = createSystemActionDbAccessors();
        }
        return systemActionDbAccessors;
    }

    public List<VentilationSystem> getVentilationSystems() {
        if (ventilationSystems == null) {
            createVentilationSystems();
        }
        return ventilationSystems;
    }

    public CurrentSensors getCurrentSensors(Sensors sensors) {
        if (currentSensors == null) {
            createCurrentSensors(sensors);
        }
        return currentSensors;
    }

    public List<Rule> getFreshAirRules(Sensors sensors) {
        if (freshAirRules == null) {
            createFreshAirRules(sensors, createDbAccessor(SystemPart.AIR_FLOW));
        }
        return freshAirRules;
    }

    private static SystemActionDbAccessors createSystemActionDbAccessors() {
        return new SystemActionDbAccessors(createDbAccessor(SystemPart.AIR_FLOW), createDbAccessor(SystemPart.HUMIDITY));
    }

    private void createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(gpioPins);
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(getSystemActionDbAccessors());
        ventilationSystems = List.of(ventilationSystem, systemActionPersistence);
    }

    private void createCurrentSensors(Sensors sensors) {
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        currentSensors = new CurrentSensors(currentIndoorSensorData, currentOutdoorSensorData);
    }

    private void createFreshAirRules(Sensors sensors, SystemActionDbAccessor dbAccessor) {
        freshAirRules = new FreshAirRuleBuilder().build(getCurrentSensors(sensors), dbAccessor);
    }

    private static GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private static SystemActionDbAccessor createDbAccessor(SystemPart systemPart) {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }
}
