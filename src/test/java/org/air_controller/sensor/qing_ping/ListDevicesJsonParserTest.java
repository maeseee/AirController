package org.air_controller.sensor.qing_ping;

import org.air_controller.sensor_values.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.air_controller.sensor.qing_ping.Devices.MAC_AIR_PRESSURE_DEVICE;
import static org.air_controller.sensor.qing_ping.Devices.MAC_CO2_DEVICE_1;
import static org.assertj.core.api.Assertions.assertThat;

class ListDevicesJsonParserTest {

    @Test
    void shouldParseSensorDataOfAirPressureDevice() throws InvalidArgumentException {
        final long epochSecondNow = Instant.now().getEpochSecond();
        final ListDevicesJsonParser testee = new ListDevicesJsonParser();

        final Optional<SensorData> result = testee.parseDeviceListResponse(getSampleDeviceListResponse(epochSecondNow), MAC_AIR_PRESSURE_DEVICE);

        final Temperature expectedTemperature = Temperature.createFromCelsius(21.5);
        final Humidity expectedHumidity = Humidity.createFromRelative(54.2, expectedTemperature);
        assertThat(result).isPresent().hasValueSatisfying(sensorData -> {
            assertThat(sensorData.temperature()).isEqualTo(expectedTemperature);
            assertThat(sensorData.humidity()).isEqualTo(expectedHumidity);
            assertThat(sensorData.co2()).isEmpty();
            assertThat(sensorData.timestamp().toEpochSecond()).isEqualTo(epochSecondNow);
        });
    }

    @Test
    void shouldParseSensorDataOfCo2Device() throws InvalidArgumentException {
        final long epochSecondNow = Instant.now().getEpochSecond();
        final ListDevicesJsonParser testee = new ListDevicesJsonParser();

        final Optional<SensorData> result = testee.parseDeviceListResponse(getSampleDeviceListResponse(epochSecondNow), MAC_CO2_DEVICE_1);

        final Temperature expectedTemperature = Temperature.createFromCelsius(22.3);
        final Humidity expectedHumidity = Humidity.createFromRelative(47.1, expectedTemperature);
        final CarbonDioxide expectedCo2 = CarbonDioxide.createFromPpm(400);
        assertThat(result).isPresent().hasValueSatisfying(sensorData -> {
            assertThat(sensorData.temperature()).isEqualTo(expectedTemperature);
            assertThat(sensorData.humidity()).isEqualTo(expectedHumidity);
            assertThat(sensorData.co2()).isPresent().hasValueSatisfying(co2 ->
                    assertThat(co2).isEqualTo(expectedCo2));
            assertThat(sensorData.timestamp().toEpochSecond()).isEqualTo(epochSecondNow);
        });
    }

    @Test
    void shouldReturnOptionalEmpty_whenInvalidMacAddress() {
        final ListDevicesJsonParser testee = new ListDevicesJsonParser();

        final Optional<SensorData> result = testee.parseDeviceListResponse(getSampleDeviceListResponse(Instant.now().getEpochSecond()), "mac");

        assertThat(result).isEmpty();
    }

    private String getSampleDeviceListResponse(long epochSecond) {
        final String SAMPLE_DEVICE_LIST_RESPONSE = """
            {
              "total": 2,
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
                      "report_interval": 600,
                      "collect_interval": 600
                    }
                  },
                  "data": {
                    "timestamp": {
                      "value": <epoch_timestamp>
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
                },
                {
                  "info": {
                    "mac": "582D34831850",
                    "product": {
                      "id": 1204,
                      "code": "CGP22C",
                      "name": "青萍二氧化碳和温湿度检测仪",
                      "en_name": "CO2\\u0026 Temp \\u0026 RH Monitor",
                      "noBleSetting": true
                    },
                    "name": "Co2",
                    "version": "2.0.0",
                    "created_at": 1706853846,
                    "group_id": 37830,
                    "group_name": "S42",
                    "status": {
                      "offline": false
                    },
                    "connection_type": "WiFi",
                    "setting": {
                      "report_interval": 600,
                      "collect_interval": 600
                    }
                  },
                  "data": {
                    "timestamp": {
                      "value": <epoch_timestamp>
                    },
                    "battery": {
                      "value": 100
                    },
                    "signal": {
                      "value": -66
                    },
                    "temperature": {
                      "value": 22.3
                    },
                    "humidity": {
                      "value": 47.1
                    },
                    "co2": {
                      "value": 400
                    }
                  }
                }
              ]
            }
            """;
        return SAMPLE_DEVICE_LIST_RESPONSE.replaceAll("<epoch_timestamp>", String.valueOf(epochSecond));
    }
}