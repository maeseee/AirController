package org.airController;

import org.airController.gpioAdapter.GpioPin;
import org.airController.persistence.SensorValuePersistenceObserver;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.OutdoorSensor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    @Mock
    private GpioPin airFlow;
    @Mock
    private GpioPin humidityExchanger;
    @Mock
    private OutdoorSensor outdoorSensor;
    @Mock
    private IndoorSensor indoorSensor;
    @Mock
    private SensorValuePersistenceObserver persistenceObserver;
    @Mock
    private ScheduledExecutorService executor;

    @Test
    void testWhenCreateApplicationThenScheduleExecutor() {
        final Application testee = new Application(airFlow, humidityExchanger, outdoorSensor, indoorSensor, persistenceObserver, executor);

        testee.run();

        verify(executor).scheduleAtFixedRate(outdoorSensor, 0, 10, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(indoorSensor, 0, 10, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(any(), eq(0L), eq(1L), eq(TimeUnit.MINUTES));
    }

}