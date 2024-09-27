package org.airController.sensor.qingPing;

import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.Temperature;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.Optional;

import static org.airController.sensor.qingPing.QingPingDevices.MAC_AIR_PRESSURE_DEVICE;
import static org.airController.sensor.qingPing.QingPingDevices.MAC_CO2_DEVICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QingPingListDevicesJsonParserTest {

    private final String SAMPLE_DEVICE_LIST_RESPONSE = """
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
                      "value": 1704516210
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

    @Test
    void testParsingDeviceListOfAirPressureDevice() {
        final QingPingListDevicesJsonParser testee = new QingPingListDevicesJsonParser();

        final Optional<QingPingSensorData> result = testee.parseDeviceListResponse(SAMPLE_DEVICE_LIST_RESPONSE, MAC_AIR_PRESSURE_DEVICE);

        assertTrue(result.isPresent());
        assertTrue(result.get().getTemperature().isPresent());
        assertTrue(result.get().getHumidity().isPresent());
        final Temperature temperature = result.get().getTemperature().get();
        assertEquals(21.5, temperature.getCelsius(), 0.1);
        final Humidity humidity = result.get().getHumidity().get();
        assertEquals(54.2, humidity.getRelativeHumidity(temperature), 0.1);
        assertEquals(1704516210, result.get().getTimeStamp().atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    @Test
    void testParsingDeviceListOfCo2Device() {
        final QingPingListDevicesJsonParser testee = new QingPingListDevicesJsonParser();

        final Optional<QingPingSensorData> result = testee.parseDeviceListResponse(SAMPLE_DEVICE_LIST_RESPONSE, MAC_CO2_DEVICE);

        assertTrue(result.isPresent());
        assertTrue(result.get().getTemperature().isPresent());
        assertTrue(result.get().getHumidity().isPresent());
        final Temperature temperature = result.get().getTemperature().get();
        assertEquals(22.3, temperature.getCelsius(), 0.1);
        assertEquals(47.1, result.get().getHumidity().get().getRelativeHumidity(temperature), 0.1);
        assertTrue(result.get().getCo2().isPresent());
        assertEquals(400, result.get().getCo2().get().getPpm(), 0.1);
        assertEquals(1704516210, result.get().getTimeStamp().atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    @Test
    void testWhenParsingDeviceListWithWringMacAddressThenOptionalEmpty() {
        final QingPingListDevicesJsonParser testee = new QingPingListDevicesJsonParser();

        final Optional<QingPingSensorData> result = testee.parseDeviceListResponse(SAMPLE_DEVICE_LIST_RESPONSE, "mac");

        assertTrue(result.isEmpty());
    }
}