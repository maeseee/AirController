package org.airController.sensor;


import org.airController.util.RaspberryPiPin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneWireCommunicationTest {

//    @Mock
//    private RaspberryPiPin raspberryPiPin;
//
//    @Test
//    void testCommunicationWhenReadSensorData() {
//        final OneWireCommunication testee = new OneWireCommunication(raspberryPiPin);
//
//        final OptionalLong sensorData = testee.readSensorData();
//
//        verify(raspberryPiPin, atLeast(1)).setMode(anyBoolean());
//        verify(raspberryPiPin, atLeast(1)).read();
//        verify(raspberryPiPin, atLeast(1)).write(anyBoolean());
//        verify(raspberryPiPin, atLeast(1)).sleep(anyInt());
//        assertTrue(sensorData.isEmpty());
//    }
//
//    @Test
//    void testValidCommunicationWhenReadSensorData() {
//        when(raspberryPiPin.read()).thenReturn(
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false,
//                true, false, true, false, true, false);
//        final OneWireCommunication testee = new OneWireCommunication(raspberryPiPin);
//
//        final OptionalLong sensorData = testee.readSensorData();
//
//        verify(raspberryPiPin, atLeast(1)).setMode(anyBoolean());
//        verify(raspberryPiPin, atLeast(1)).read();
//        verify(raspberryPiPin, atLeast(1)).write(anyBoolean());
//        verify(raspberryPiPin, atLeast(1)).sleep(anyInt());
//        assertTrue(sensorData.isPresent());
//    }

}