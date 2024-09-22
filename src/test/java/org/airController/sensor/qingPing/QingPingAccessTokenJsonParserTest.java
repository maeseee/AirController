package org.airController.sensor.qingPing;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QingPingAccessTokenJsonParserTest {

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
        final QingPingAccessTokenJsonParser testee = new QingPingAccessTokenJsonParser();

        final Optional<QingPingAccessTokenData> result = testee.parse(sampleAccessTokenResponse);

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
        final QingPingAccessTokenJsonParser testee = new QingPingAccessTokenJsonParser();

        final Optional<QingPingAccessTokenData> result = testee.parse(sampleAccessTokenResponse);

        assertFalse(result.isPresent());
    }
}