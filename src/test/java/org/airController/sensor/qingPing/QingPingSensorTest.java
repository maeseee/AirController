package org.airController.sensor.qingPing;

import org.airController.controllers.SensorData;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.InvaildArgumentException;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QingPingSensorTest {

    @Captor
    ArgumentCaptor<SensorData> indoorSensorDataArgumentCaptor;

    @Test
    void testWhenRunThenNotifyObservers() throws InvaildArgumentException, CommunicationException, IOException, URISyntaxException {
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
    void testWhenInvalidSensorDataThenDoNotNotifyObservers() throws CommunicationException, IOException, URISyntaxException {
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

    @ParameterizedTest(name = "{index} => temperature1={0}, humidity1={1}, co2_1={2}, age_1={3}, temperatureExp={4}, humidityExp={5}")
    @ArgumentsSource(SensorDataArgumentProvider.class)
    void testWhenMultipleSensorsWithoutCo2ThenAverage(double temperature1, double humidity1, CarbonDioxide co2, int age_1, double temperatureExp,
            double humidityExp)
            throws InvaildArgumentException, CommunicationException, IOException, URISyntaxException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.readToken()).thenReturn("token");
        final Temperature temperature = Temperature.createFromCelsius(temperature1);
        final Humidity humidity = Humidity.createFromAbsolute(humidity1);
        final LocalDateTime time1 = LocalDateTime.now().minusMinutes(age_1);
        final QingPingSensorData sensorData1 = new QingPingSensorData(temperature, humidity, co2, time1);
        final QingPingSensorData sensorData2 =
                new QingPingSensorData(Temperature.createFromCelsius(40.0), Humidity.createFromAbsolute(15.0), LocalDateTime.now());
        final QingPingListDevices listDevices = mock(QingPingListDevices.class);
        when(listDevices.readSensorDataList(any())).thenReturn(List.of(sensorData1, sensorData2));
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevices);
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorData(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        assertThat(indoorSensorDataCapture.getTemperature()).isPresent().hasValue(Temperature.createFromCelsius(temperatureExp));
        assertThat(indoorSensorDataCapture.getHumidity()).isPresent().hasValue(Humidity.createFromAbsolute(humidityExp));
    }

    static class SensorDataArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws InvaildArgumentException {
            return Stream.of(
                    Arguments.of(20.0, 10.0, null, 0, 30.0, 12.5),
                    Arguments.of(20.0, 10.0, CarbonDioxide.createFromPpm(500.0), 0, 30.0, 12.5),
                    Arguments.of(40.0, 15.0, CarbonDioxide.createFromPpm(500.0), 0, 40.0, 15.0),
                    Arguments.of(20.0, 10.0, null, 30, 30.0, 12.5),
                    Arguments.of(20.0, 10.0, null, 59, 30.0, 12.5),
                    Arguments.of(20.0, 10.0, null, 60, 40.0, 15.0),
                    Arguments.of(20.0, 10.0, null, 100, 40.0, 15.0)
            );
        }
    }
}