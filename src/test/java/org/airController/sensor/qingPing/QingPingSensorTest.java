package org.airController.sensor.qingPing;

import org.airController.sensor.IndoorSensorObserver;
import org.airController.sensorValues.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QingPingSensorTest {

    @Captor
    ArgumentCaptor<SensorData> indoorSensorDataArgumentCaptor;

    @Test
    void shouldNotifyObservers_whenRun() throws InvalidArgumentException, CommunicationException, IOException, URISyntaxException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final QingPingListDevices listDevices = mock(QingPingListDevices.class);
        final Temperature temperature = Temperature.createFromCelsius(21.5);
        final Humidity humidity = Humidity.createFromAbsolute(10.0);
        final LocalDateTime time1 = LocalDateTime.now();
        final QingPingSensorData sensorData = new QingPingSensorData(temperature, humidity, null, time1);
        when(listDevices.readSensorDataList(any())).thenReturn(List.of(sensorData));
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevices);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorData(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertThat(indoorSensorDataCapture.getTemperature()).isPresent().hasValue(Temperature.createFromCelsius(21.5));
        assertThat(indoorSensorDataCapture.getHumidity()).isPresent().hasValue(Humidity.createFromAbsolute(10.0));
    }

    @Test
    void shouldNotNotifyObservers_whenInvalidSensorData() throws CommunicationException, IOException, URISyntaxException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final QingPingListDevices listDevices = mock(QingPingListDevices.class);
        when(listDevices.readSensorDataList(any())).thenReturn(new ArrayList<>());
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevices);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }

    @ParameterizedTest(
            name = "{index} => temperature1 ={0}, humidity1={1}, co2_1={2}, minutesYounger={3}, expectedTemperature={4}, expectedHumidity={5}, " +
                    "expectedCo2={6}")
    @CsvSource({
            "20, 10, NaN, 0, 30, 12.5, NaN",
            "20, 10, 500, 0, 30, 12.5, 500",
            "40, 15, 500.0, 0, 40, 15.0, 500",
            "20, 10, NaN, 30, 30, 12.5, NaN",
            "20, 10, NaN, 59, 30, 12.5, NaN",
            "20, 10, NaN, 60, 40, 15.0, NaN", // Invalid after SENSOR_INVALIDATION_TIME
    })
    void shouldTakeAverageOfSensorValues_whenMultipleSensors(double temperature1, double humidity1, double co2_1, int minutesYounger,
            double expectedTemperature, double expectedHumidity, double expectedCo2)
            throws InvalidArgumentException, CommunicationException, IOException, URISyntaxException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final LocalDateTime now = LocalDateTime.now();
        final QingPingSensorData sensorData1 = createSensorData(temperature1, humidity1, co2_1, minutesYounger, now);
        final QingPingSensorData sensorData2 = createSensorData(40.0, 15.0, Double.NaN, 0, now);
        final QingPingListDevices listDevices = mock(QingPingListDevices.class);
        when(listDevices.readSensorDataList(any())).thenReturn(List.of(sensorData1, sensorData2));
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevices);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        final SensorData expectedSensorData = createSensorData(expectedTemperature, expectedHumidity, expectedCo2, 0, now);
        verify(observer).updateIndoorSensorData(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertThat(indoorSensorDataCapture.getTemperature()).isEqualTo(expectedSensorData.getTemperature());
        assertThat(indoorSensorDataCapture.getHumidity()).isEqualTo(expectedSensorData.getHumidity());
        assertThat(indoorSensorDataCapture.getCo2()).isEqualTo(expectedSensorData.getCo2());
        assertThat(indoorSensorDataCapture.getTimeStamp()).isEqualTo(expectedSensorData.getTimeStamp());
    }

    private static QingPingSensorData createSensorData(double temperature, double humidity, double co2, int minutesYounger, LocalDateTime now)
            throws InvalidArgumentException {
        final Temperature temp = Temperature.createFromCelsius(temperature);
        final Humidity hum = Humidity.createFromAbsolute(humidity);
        final CarbonDioxide carbonDioxide = Double.isNaN(co2) ? null : CarbonDioxide.createFromPpm(co2);
        final LocalDateTime timestamp = now.minusMinutes(minutesYounger);
        return new QingPingSensorData(temp, hum, carbonDioxide, timestamp);
    }
}