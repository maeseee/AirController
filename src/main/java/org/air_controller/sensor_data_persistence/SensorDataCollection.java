package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.ClimateDataPoint;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SensorDataCollection implements SensorDataPersistence {
    private final List<SensorDataPersistence> persistenceList;

    public SensorDataCollection(List<SensorDataPersistence> persistenceList) {
        this.persistenceList = persistenceList;
    }

    @Override
    public void persist(ClimateDataPoint dataPoint) {
        persistenceList.forEach(persistence -> persistence.persist(dataPoint));
    }

    @Override
    public List<ClimateDataPoint> read() {
        return persistenceList.getFirst().read();
    }

    @Override
    public Optional<ClimateDataPoint> getMostCurrentClimateDataPoint(ZonedDateTime lastValidTimestamp) {
        return persistenceList.getFirst().getMostCurrentClimateDataPoint(lastValidTimestamp);
    }
}
