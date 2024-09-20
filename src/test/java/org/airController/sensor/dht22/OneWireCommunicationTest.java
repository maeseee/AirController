package org.airController.sensor.dht22;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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