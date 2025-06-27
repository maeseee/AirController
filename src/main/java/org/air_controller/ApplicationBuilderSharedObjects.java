package org.air_controller;

import lombok.Setter;
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
import org.air_controller.system.SystemStatistics;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemActionPersistence;
import org.air_controller.system_action.SystemPart;

import java.util.List;

@Setter
class ApplicationBuilderSharedObjects {
    private SystemActionDbAccessors systemActionDbAccessors;
    private List<VentilationSystem> ventilationSystems;
    private CurrentSensors currentSensors;
    private List<Rule> freshAirRules;
    private GpioPins gpioPins;

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

    public List<Rule> getFreshAirRules(Sensors sensors, SystemStatistics statistics) {
        if (freshAirRules == null) {
            createFreshAirRules(sensors, statistics);
        }
        return freshAirRules;
    }

    private GpioPins getGpioPins() {
        if (gpioPins == null) {
            createDingtianPins();
        }
        return gpioPins;
    }

    private SystemActionDbAccessors createSystemActionDbAccessors() {
        return new SystemActionDbAccessors(getDbAccessor(SystemPart.AIR_FLOW), getDbAccessor(SystemPart.HUMIDITY));
    }

    private void createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(getGpioPins());
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(getSystemActionDbAccessors());
        ventilationSystems = List.of(ventilationSystem, systemActionPersistence);
    }

    private void createCurrentSensors(Sensors sensors) {
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        currentSensors = new CurrentSensors(currentIndoorSensorData, currentOutdoorSensorData);
    }

    private void createFreshAirRules(Sensors sensors, SystemStatistics statistics) {
        freshAirRules = new FreshAirRuleBuilder().build(getCurrentSensors(sensors), statistics);
    }

    private void createDingtianPins() {
        gpioPins = new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private SystemActionDbAccessor getDbAccessor(SystemPart systemPart) {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }
}
