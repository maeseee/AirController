package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.ClimateDataPoint;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ClimateDataPointPersistence {

    String INDOOR_TABLE_NAME = "indoorSensor";
    String OUTDOOR_TABLE_NAME = "outdoorSensor";

    void persist(ClimateDataPoint dataPoint);

    List<ClimateDataPoint> read();

    Optional<ClimateDataPoint> getMostCurrentClimateDataPoint(ZonedDateTime lastValidTimestamp);

    List<ClimateDataPoint> getDataPointsFromLast24Hours();
}
