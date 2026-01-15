package org.air_controller;

import lombok.Getter;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_data_persistence.ClimateSensorAccessors;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemDbAccessors;
import org.air_controller.system_action.SystemPart;

public class ApplicationPersistence {
    @Getter
    private final VentilationSystemDbAccessors ventilationSystemDbAccessors;
    @Getter
    private final ClimateSensorAccessors climateSensorAccessors = createClimateSensorAccessors();


    ApplicationPersistence() {
        this(createSystemActionDbAccessors());
    }

    ApplicationPersistence(VentilationSystemDbAccessors ventilationSystemDbAccessors) {
        this.ventilationSystemDbAccessors = ventilationSystemDbAccessors;
    }

    private static VentilationSystemDbAccessors createSystemActionDbAccessors() {
        return new VentilationSystemDbAccessors(
                createSystemActionDbAccessor(SystemPart.AIR_FLOW),
                createSystemActionDbAccessor(SystemPart.HUMIDITY));
    }

    private static SystemActionDbAccessor createSystemActionDbAccessor(SystemPart systemPart) {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }

    private ClimateSensorAccessors createClimateSensorAccessors() {
        return new ClimateSensorAccessors(
                createClimateSensorAccessor(ClimateDataPointPersistence.INDOOR_TABLE_NAME),
                createClimateSensorAccessor(ClimateDataPointPersistence.OUTDOOR_TABLE_NAME));
    }

    private ClimateDataPointPersistence createClimateSensorAccessor(String tableName) {
        return new ClimateDataPointsDbAccessor(new MariaDatabase(), tableName);
    }
}
