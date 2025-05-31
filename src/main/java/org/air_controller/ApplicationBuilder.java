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
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system.VentilationSystemTimeKeeper;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemActionPersistence;
import org.air_controller.system_action.SystemPart;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Setter
public class ApplicationBuilder {

    private Sensors sensors;
    private RuleApplier ruleApplier;
    private VentilationSystemTimeKeeper timeKeeper;
    private ScheduledExecutorService executor;

    private SystemActionDbAccessors systemActionDbAccessors;

    public Application build() throws SQLException {
        createNotMockedObjects();
        return new Application(sensors, ruleApplier, timeKeeper, executor);
    }

    private void createNotMockedObjects() throws SQLException {
        if (sensors == null) {
            sensors = new SensorsBuilder().build();
        }
        if (systemActionDbAccessors == null) {
            systemActionDbAccessors = createSystemActionDbAccessors();
        }
        if (timeKeeper == null) {
            timeKeeper = new VentilationSystemTimeKeeper(systemActionDbAccessors.airFlow());
        }
        if (ruleApplier == null) {
            final GpioPins gpioPins = createDingtianPins();
            final VentilationSystem ventilationSystem = createVEntilationSystem(gpioPins);
            ruleApplier =
                    new RuleApplierBuilder().build(ventilationSystem, sensors, timeKeeper, new SystemActionPersistence(systemActionDbAccessors));
        }
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(1);
        }
    }

    private GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    private static SystemActionDbAccessors createSystemActionDbAccessors() throws SQLException {
        return new SystemActionDbAccessors(createDbAccessor(SystemPart.AIR_FLOW), createDbAccessor(SystemPart.HUMIDITY));
    }

    private static SystemActionDbAccessor createDbAccessor(SystemPart systemPart) throws SQLException {
        return new SystemActionDbAccessor(Persistence.createConnection(), systemPart);
    }

    private VentilationSystem createVEntilationSystem(GpioPins gpioPins) {
        return new ControlledVentilationSystem(gpioPins);
    }
}
