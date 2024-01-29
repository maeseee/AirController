package org.airController.sensor;

import org.airController.entities.AirValue;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonQingPingParserTest {

    private final String SAMPLE_DEVICE_LIST_RESPONSE = """
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

    @Test
    void testParsingAccessToken() {
        final String sampleAccessTokenResponse = """
                {
                "access_token": "5E05GrH9bv-yVbtzpbgrHt2sXLl6SKNUJCYNizY2E58.FpNVQZjkKka1Yn7bgxlAHJ-V-33DD3J-pz_hRwMa_gY",
                "expires_in": 7199,
                "scope": "device_full_access",
                "token_type": "bearer"
                }
                """;
        final JsonQingPingParser testee = new JsonQingPingParser();

        final Optional<QingPingAccessToken> result = testee.parseAccessTokenResponse(sampleAccessTokenResponse);

        assertTrue(result.isPresent());
        assertEquals("5E05GrH9bv-yVbtzpbgrHt2sXLl6SKNUJCYNizY2E58.FpNVQZjkKka1Yn7bgxlAHJ-V-33DD3J-pz_hRwMa_gY", result.get().accessToken());
        assertEquals(7199, result.get().expiresIn());
    }

    @Test
    void testWhenAccessTokenMissingInResponseThenOptionalEmpty() {
        final String sampleAccessTokenResponse = """
                {
                }
                """;
        final JsonQingPingParser testee = new JsonQingPingParser();

        final Optional<QingPingAccessToken> result = testee.parseAccessTokenResponse(sampleAccessTokenResponse);

        assertFalse(result.isPresent());
    }

    @Test
    void testParsingDeviceList() {
        final JsonQingPingParser testee = new JsonQingPingParser();

        final Optional<AirValue> result = testee.parseDeviceListResponse(SAMPLE_DEVICE_LIST_RESPONSE, "582D3480A7F4");

        assertTrue(result.isPresent());
        assertEquals(21.5, result.get().getTemperature().getCelsius(), 0.1);
        assertEquals(54.2, result.get().getHumidity().getRelativeHumidity(), 0.1);
        assertEquals(1704516210, result.get().getTimeStamp().atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    @Test
    void testWhenParsingDeviceListWithWringMacAddressThenOptionalEmpty() {
        final JsonQingPingParser testee = new JsonQingPingParser();

        final Optional<AirValue> result = testee.parseDeviceListResponse(SAMPLE_DEVICE_LIST_RESPONSE, "mac");

        assertTrue(result.isEmpty());
    }
}