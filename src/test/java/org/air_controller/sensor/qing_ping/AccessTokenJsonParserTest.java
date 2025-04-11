package org.air_controller.sensor.qing_ping;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccessTokenJsonParserTest {

    @Test
    void shouldParsAccessToken() {
        final String sampleAccessTokenResponse = """
                {
                "access_token": "5E05GrH9bv-yVbtzpbgrHt2sXLl6SKNUJCYNizY2E58.FpNVQZjkKka1Yn7bgxlAHJ-V-33DD3J-pz_hRwMa_gY",
                "expires_in": 7199,
                "scope": "device_full_access",
                "token_type": "bearer"
                }
                """;
        final AccessTokenJsonParser testee = new AccessTokenJsonParser();

        final Optional<AccessTokenData> result = testee.parse(sampleAccessTokenResponse);

        assertThat(result).isPresent().hasValueSatisfying(accessTokenData -> {
            assertThat(accessTokenData.accessToken()).isEqualTo(
                    "5E05GrH9bv-yVbtzpbgrHt2sXLl6SKNUJCYNizY2E58.FpNVQZjkKka1Yn7bgxlAHJ-V-33DD3J-pz_hRwMa_gY");
            assertThat(accessTokenData.expiresIn()).isEqualTo(7199);
        });
    }

    @Test
    void shouldReturnOptionalEmpty_whenAccessTokenMissingInResponse() {
        final String sampleAccessTokenResponse = """
                {
                }
                """;
        final AccessTokenJsonParser testee = new AccessTokenJsonParser();

        final Optional<AccessTokenData> result = testee.parse(sampleAccessTokenResponse);

        assertThat(result).isEmpty();
    }
}