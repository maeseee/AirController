package org.air_controller.sensor_values;

import org.air_controller.sensor_data_persistence.SensorDataCollection;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataCollectionTest {

    private final List<SensorDataPersistence> persistenceList = new ArrayList<>();
    @Mock
    private SensorDataPersistence persistence1;
    @Mock
    private SensorDataPersistence persistence2;
    @Mock
    private ClimateDataPoint dataPoint;

    @BeforeEach
    void setUp() {
        persistenceList.clear();
        persistenceList.add(persistence1);
        persistenceList.add(persistence2);
    }

    @Test
    void shouldPersistOnAllPersistenceEntries() {
        final SensorDataCollection testee = new SensorDataCollection(persistenceList);

        testee.persist(dataPoint);

        verify(persistence1).persist(dataPoint);
        verify(persistence2).persist(dataPoint);
        verifyNoMoreInteractions(persistence1);
        verifyNoMoreInteractions(persistence2);
    }

    @Test
    void shouldReadFromFirstPersistence() {
        when(persistence1.read()).thenReturn(List.of(dataPoint));
        final SensorDataCollection testee = new SensorDataCollection(persistenceList);

        final List<ClimateDataPoint> climateDataPointResult = testee.read();

        assertThat(climateDataPointResult).contains(dataPoint);
        verify(persistence1).read();
        verifyNoMoreInteractions(persistence1);
        verifyNoInteractions(persistence2);
    }

    @Test
    void shouldReturnMostCurrentSensorDataFromFirstPersistence() {
        when(persistence1.getMostCurrentClimateDataPoint(any())).thenReturn(Optional.of(dataPoint));
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final SensorDataCollection testee = new SensorDataCollection(persistenceList);

        final Optional<ClimateDataPoint> mostCurrentSensorData = testee.getMostCurrentClimateDataPoint(now);

        assertThat(mostCurrentSensorData).contains(dataPoint);
        verify(persistence1).getMostCurrentClimateDataPoint(now);
        verifyNoMoreInteractions(persistence1);
        verifyNoInteractions(persistence2);
    }
}
