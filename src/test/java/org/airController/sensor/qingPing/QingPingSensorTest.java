package org.airController.sensor.qingPing;

import org.airController.controllers.SensorData;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
import org.airController.entities.InvaildArgumentException;
import org.airController.entities.Temperature;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.assertj.core.api.Assertions;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QingPingSensorTest {

    private final static String SAMPLE_ACCESS_TOKEN_RESPONSE = """
            {
                "access_token": "tYYQPa8MIZax-9J8DXErfiE_k9II4GOpDBerco2XfCQ.-7cROIxh0DvPJ53QJc-ZOSHrfkNDdMcdtbU9aW1wjjw",
                "expires_in": 7199,
                "scope": "device_full_access",
                "token_type": "bearer"
            }
            """;
    private final static String SAMPLE_LIST_DEVICES_RESPONSE = String.format("""
            {
                "total": 1,
                "devices": [
                    {
                        "info": {
                            "mac": "582D3480A7F4",
                            "product": {
                                "id": 1101,
                                "code": "CGP1N",
                                "name": "青萍温湿度气压计",
                                "en_name": "Qingping Temp & RH Barometer",
                                "noBleSetting": false
                            },
                            "name": "Wohnzimmer",
                            "version": "2.0.0",
                            "created_at": 1704390261,
                            "group_id": 37830,
                            "group_name": "S42",
                            "status": {
                                "offline": false
                            },
                            "connection_type": "WiFi",
                            "setting": {
                                "report_interval": 7200,
                                "collect_interval": 600
                            }
                        },
                        "data": {
                            "timestamp": {
                                "value": %d
                            },
                            "battery": {
                                "value": 86
                            },
                            "signal": {
                                "value": -42
                            },
                            "temperature": {
                                "value": 21.5
                            },
                            "humidity": {
                                "value": 54.2
                            },
                            "pressure": {
                                "value": 93.64
                            }
                        }
                    }
                ]
            }
            """, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());

    @Captor
    ArgumentCaptor<SensorData> indoorSensorDataArgumentCaptor;

    @Test
    void testWhenRunThenNotifyObservers() throws InvaildArgumentException, CommunicationException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.getToken()).thenReturn("token");
        final QingPingListDevicesRequest listDevicesRequest = mock(QingPingListDevicesRequest.class);
        when(listDevicesRequest.sendRequest(any())).thenReturn(Optional.of(SAMPLE_LIST_DEVICES_RESPONSE));
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevicesRequest, new QingPingJsonDeviceListParser(),
                singletonList(QingPingSensor.MAC_PRESSURE_DEVICE));
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorData(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        Assertions.assertThat(indoorSensorDataCapture.getTemperature()).isPresent().hasValue(Temperature.createFromCelsius(21.5));
        final Temperature expectedTemperature = Temperature.createFromCelsius(21.5);
        Assertions.assertThat(indoorSensorDataCapture.getHumidity()).isPresent().hasValue(Humidity.createFromRelative(54.2, expectedTemperature));
    }

    @Test
    void testWhenInvalidSensorDataThenDoNotNotifyObservers() throws CommunicationException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.getToken()).thenReturn("token");
        final QingPingListDevicesRequest listDevicesRequest = mock(QingPingListDevicesRequest.class);
        when(listDevicesRequest.sendRequest(any())).thenReturn(Optional.empty());
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevicesRequest, new QingPingJsonDeviceListParser(),
                singletonList(QingPingSensor.MAC_PRESSURE_DEVICE));
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }

    @ParameterizedTest(name = "{index} => temperature1={0}, humidity1={1}, co2_1={2}, age_1={3}, temperatureExp={4}, humidityExp={5}")
    @ArgumentsSource(SensorDataArgumentProvider.class)
    void testWhenMultipleSensorsWithoutCo2ThenAverage(double temperature1, double humidity1, CarbonDioxide co2, int age_1, double temperatureExp,
                                                      double humidityExp) throws InvaildArgumentException, CommunicationException {
        final QingPingAccessToken accessToken = mock(QingPingAccessToken.class);
        when(accessToken.getToken()).thenReturn("token");
        final QingPingListDevicesRequest listDevicesRequest = mock(QingPingListDevicesRequest.class);
        when(listDevicesRequest.sendRequest(any())).thenReturn(Optional.of(SAMPLE_LIST_DEVICES_RESPONSE));
        final QingPingJsonDeviceListParser parser = mock(QingPingJsonDeviceListParser.class);
        final Temperature temperature = Temperature.createFromCelsius(temperature1);
        final Humidity humidity = Humidity.createFromAbsolute(humidity1);
        final LocalDateTime time1 = LocalDateTime.now().minusMinutes(age_1);
        final QingPingSensorData sensorData1 = new QingPingSensorData(temperature, humidity, co2, time1);
        final QingPingSensorData sensorData2 = new QingPingSensorData(Temperature.createFromCelsius(40.0), Humidity.createFromAbsolute(15.0), LocalDateTime.now());
        when(parser.parseDeviceListResponse(any(), eq("mac1"))).thenReturn(Optional.of(sensorData1));
        when(parser.parseDeviceListResponse(any(), eq("mac2"))).thenReturn(Optional.of(sensorData2));
        final QingPingSensor testee = new QingPingSensor(accessToken, listDevicesRequest, parser, asList("mac1", "mac2"));
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorSensorData(indoorSensorDataArgumentCaptor.capture());
        final SensorData indoorSensorDataCapture = indoorSensorDataArgumentCaptor.getValue();
        Assertions.assertThat(indoorSensorDataCapture.getTemperature()).isPresent().hasValue(Temperature.createFromCelsius(temperatureExp));
        Assertions.assertThat(indoorSensorDataCapture.getHumidity()).isPresent().hasValue(Humidity.createFromAbsolute(humidityExp));
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