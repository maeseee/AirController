package org.air_controller;

import lombok.Getter;
import org.air_controller.gpio.GpioPins;
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

import java.util.List;

class ApplicationBuilderSharedObjects {
    private final GpioPins gpioPins;
    @Getter
    private final SystemActionDbAccessors systemActionDbAccessors;
    @Getter
    private final List<VentilationSystem> ventilationSystems;
    private CurrentSensors currentSensors;
    private List<Rule> freshAirRules;

    public ApplicationBuilderSharedObjects(GpioPins gpioPins) {
        this.gpioPins = gpioPins;
        this.systemActionDbAccessors = createSystemActionDbAccessors();
        this.ventilationSystems = createVentilationSystems();
    }

    public CurrentSensors getOrCreateCurrentSensors(Sensors sensors) {
        if (currentSensors == null) {
            createCurrentSensors(sensors);
        }
        return currentSensors;
    }

    public List<Rule> getOrCreateFreshAirRules(Sensors sensors) {
        if (freshAirRules == null) {
            createFreshAirRules(sensors, createDbAccessor(SystemPart.AIR_FLOW));
        }
        return freshAirRules;
    }

    private static SystemActionDbAccessors createSystemActionDbAccessors() {
        return new SystemActionDbAccessors(createDbAccessor(SystemPart.AIR_FLOW), createDbAccessor(SystemPart.HUMIDITY));
    }

    private List<VentilationSystem> createVentilationSystems() {
        final VentilationSystem ventilationSystem = new ControlledVentilationSystem(gpioPins);
        final SystemActionPersistence systemActionPersistence = new SystemActionPersistence(getSystemActionDbAccessors());
        return List.of(ventilationSystem, systemActionPersistence);
    }

    private void createCurrentSensors(Sensors sensors) {
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        currentSensors = new CurrentSensors(currentIndoorSensorData, currentOutdoorSensorData);
    }

    private void createFreshAirRules(Sensors sensors, SystemActionDbAccessor dbAccessor) {
        freshAirRules = new FreshAirRuleBuilder().build(getOrCreateCurrentSensors(sensors), dbAccessor);
    }

    private static SystemActionDbAccessor createDbAccessor(SystemPart systemPart) {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }
}
