package org.airController;

import org.airController.controllers.FreshAirController;
import org.airController.rules.TimeKeeper;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.OutdoorSensor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    @Mock
    private OutdoorSensor outdoorSensor;
    @Mock
    private IndoorSensor indoorSensor;
    @Mock
    private FreshAirController freshAirController;
    @Mock
    private TimeKeeper timeKeeper;
    @Mock
    private ScheduledExecutorService executor;

    @Test
    void testWhenCreateApplicationThenScheduleExecutor() {
        final Application testee = new Application(outdoorSensor, indoorSensor, freshAirController, timeKeeper, executor);

        testee.run();

        verify(executor).scheduleAtFixedRate(outdoorSensor, 0, 10, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(indoorSensor, 0, 10, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(freshAirController, 0, 1, TimeUnit.MINUTES);
        verify(executor).scheduleAtFixedRate(eq(timeKeeper), anyLong(), eq(86400L), eq(TimeUnit.SECONDS));
    }
}