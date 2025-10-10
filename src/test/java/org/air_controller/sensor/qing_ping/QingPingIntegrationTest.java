package org.air_controller.sensor.qing_ping;

import org.air_controller.secrets.Secret;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.air_controller.sensor.qing_ping.AccessToken.*;
import static org.air_controller.sensor.qing_ping.Devices.MAC_AIR_PRESSURE_DEVICE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
class QingPingIntegrationTest {

    @Test
    void shouldTestQingPingDevice() throws URISyntaxException, CommunicationException, IOException {
        final String accessTokenResponse = runAccessTokenRequest();
        final AccessTokenData accessTokenData = runParseAccessToken(accessTokenResponse);
        final String listDevicesResponse = runListDevicesRequest(accessTokenData);
        final HwSensorData sensorData = runParseListDevices(listDevicesResponse);
        assertNotNull(sensorData);
        assertTrue(ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(15).isBefore(sensorData.getTimeStamp()));
        assertTrue(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(5).isAfter(sensorData.getTimeStamp()));
    }

    private String runAccessTokenRequest() throws URISyntaxException {
        final String appSecret = Secret.getSecret(ENVIRONMENT_VARIABLE_APP_SECRET, ENCRYPTED_APP_SECRET);
        final String credentials = APP_KEY + ":" + appSecret;
        final String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        final String url = "https://oauth.cleargrass.com/oauth2/token";
        final URI uri = new URI(url);
        final AccessTokenRequest accessTokenRequest = new AccessTokenRequest(uri, base64Credentials);

        final Optional<String> accessTokenResponse = accessTokenRequest.sendRequest();

        assertTrue(accessTokenResponse.isPresent());
        return accessTokenResponse.get();
    }

    private AccessTokenData runParseAccessToken(String qingPingAccessTokenResponse) {
        final AccessTokenJsonParser parser = new AccessTokenJsonParser();
        final Optional<AccessTokenData> qingPingAccessToken = parser.parse(qingPingAccessTokenResponse);

        assertTrue(qingPingAccessToken.isPresent());
        return qingPingAccessToken.get();
    }

    private String runListDevicesRequest(AccessTokenData accessTokenData) throws URISyntaxException, CommunicationException, IOException {
        final String url = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(url);
        final ListDevicesRequest listDevicesRequest = new ListDevicesRequest(uri);

        return listDevicesRequest.sendRequest(accessTokenData.accessToken());
    }

    private HwSensorData runParseListDevices(String listDevicesResponse) {
        final ListDevicesJsonParser parser = new ListDevicesJsonParser();
        final Optional<HwSensorData> sensorData = parser.parseDeviceListResponse(listDevicesResponse, MAC_AIR_PRESSURE_DEVICE);

        assertTrue(sensorData.isPresent());
        return sensorData.get();
    }
}