package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.entities.CarbonDioxide;
import org.airController.entities.Humidity;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private final static String SAMPLE_LIST_DEVICES_RESPONSE = """
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
                                "value": 1704516210
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
            """;

    @Captor
    ArgumentCaptor<AirValue> indoorAirValueArgumentCaptor;

    @Test
    void testWhenRunThenNotifyObservers() throws IOException {
        final QingPingAccessTokenRequest accessTokenRequest = mock(QingPingAccessTokenRequest.class);
        when(accessTokenRequest.sendRequest()).thenReturn(Optional.of(SAMPLE_ACCESS_TOKEN_RESPONSE));
        final QingPingListDevicesRequest listDevicesRequest = mock(QingPingListDevicesRequest.class);
        when(listDevicesRequest.sendRequest(any())).thenReturn(Optional.of(SAMPLE_LIST_DEVICES_RESPONSE));
        final QingPingSensor testee =
                new QingPingSensor(accessTokenRequest, listDevicesRequest, new JsonQingPingParser(), singletonList("582D3480A7F4"));
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorAirValue(indoorAirValueArgumentCaptor.capture());
        final AirValue indoorAirValueCapture = indoorAirValueArgumentCaptor.getValue();
        final AirValue indoorAirValue = new AirValue(Temperature.createFromCelsius(21.5), Humidity.createFromRelative(54.2));
        assertEquals(indoorAirValue, indoorAirValueCapture);
    }

    @Test
    void testWhenInvalidSensorDataThenDoNotNotifyObservers() {
        final QingPingAccessTokenRequest accessTokenRequest = mock(QingPingAccessTokenRequest.class);
        when(accessTokenRequest.sendRequest()).thenReturn(Optional.empty());
        final QingPingListDevicesRequest listDevicesRequest = mock(QingPingListDevicesRequest.class);
        when(listDevicesRequest.sendRequest(any())).thenReturn(Optional.empty());
        final QingPingSensor testee =
                new QingPingSensor(accessTokenRequest, listDevicesRequest, new JsonQingPingParser(), singletonList("582D3480A7F4"));
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }

    @ParameterizedTest(name = "{index} => temperature1={0}, humidity1={1}, co2_1={2}, age_1={3}, temperatureExp={4}, humidityExp={5}")
    @ArgumentsSource(AirValueArgumentProvider.class)
    void testWhenMultipleSensorsWithoutCo2ThenAverage(double temperature1, double humidity1, CarbonDioxide co2, int age_1, double temperatureExp,
                                                      double humidityExp) throws IOException {
        final QingPingAccessTokenRequest accessTokenRequest = mock(QingPingAccessTokenRequest.class);
        when(accessTokenRequest.sendRequest()).thenReturn(Optional.of(SAMPLE_ACCESS_TOKEN_RESPONSE));
        final QingPingListDevicesRequest listDevicesRequest = mock(QingPingListDevicesRequest.class);
        when(listDevicesRequest.sendRequest(any())).thenReturn(Optional.of(SAMPLE_LIST_DEVICES_RESPONSE));
        final JsonQingPingParser parser = mock(JsonQingPingParser.class);
        final Temperature temperature = Temperature.createFromCelsius(temperature1);
        final Humidity humidity = Humidity.createFromRelative(humidity1);
        final LocalDateTime time1 = LocalDateTime.now().minusMinutes(age_1);
        final AirValue airValue1 = new AirValue(temperature, humidity, co2, time1);
        final AirValue airValue2 = new AirValue(Temperature.createFromCelsius(40.0), Humidity.createFromRelative(60.0), LocalDateTime.now());
        when(parser.parseDeviceListResponse(any(), eq("mac1"))).thenReturn(Optional.of(airValue1));
        when(parser.parseDeviceListResponse(any(), eq("mac2"))).thenReturn(Optional.of(airValue2));
        final QingPingSensor testee = new QingPingSensor(accessTokenRequest, listDevicesRequest, parser, asList("mac1", "mac2"));
        final IndoorSensorObserver observer = mock(IndoorSensorObserver.class);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateIndoorAirValue(indoorAirValueArgumentCaptor.capture());
        final AirValue indoorAirValueCapture = indoorAirValueArgumentCaptor.getValue();
        final AirValue
                indoorAirValue = new AirValue(Temperature.createFromCelsius(temperatureExp), Humidity.createFromRelative(humidityExp), co2, time1);
        assertEquals(indoorAirValue, indoorAirValueCapture);
    }

    static class AirValueArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            return Stream.of(
                    Arguments.of(20.0, 40.0, null, 0, 30.0, 50.0),
                    Arguments.of(20.0, 40.0, CarbonDioxide.createFromPpm(500.0), 0, 30.0, 50.0),
                    Arguments.of(40.0, 60.0, CarbonDioxide.createFromPpm(500.0), 0, 40.0, 60.0),
                    Arguments.of(20.0, 40.0, null, 30, 30.0, 50.0),
                    Arguments.of(20.0, 40.0, null, 59, 30.0, 50.0),
                    Arguments.of(20.0, 40.0, null, 60, 40.0, 60.0),
                    Arguments.of(20.0, 40.0, null, 100, 40.0, 60.0)
            );
        }
    }
}