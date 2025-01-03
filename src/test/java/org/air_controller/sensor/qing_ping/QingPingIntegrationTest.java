package org.air_controller.sensor.qing_ping;

import org.air_controller.secrets.Secret;
import org.junit.jupiter.api.Test;
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

import static org.air_controller.sensor.qing_ping.QingPingAccessToken.*;
import static org.air_controller.sensor.qing_ping.QingPingDevices.MAC_AIR_PRESSURE_DEVICE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class QingPingIntegrationTest {

    @Test
    void shouldTestQingPingDevice() throws URISyntaxException, CommunicationException, IOException {
        final String accessTokenResponse = runAccessTokenRequest();
        final QingPingAccessTokenData accessTokenData = runParseAccessToken(accessTokenResponse);
        final String listDevicesResponse = runListDevicesRequest(accessTokenData);
        final QingPingSensorData sensorData = runParseListDevices(listDevicesResponse);
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
        final QingPingAccessTokenRequest accessTokenRequest = new QingPingAccessTokenRequest(uri, base64Credentials);

        final Optional<String> accessTokenResponse = accessTokenRequest.sendRequest();

        assertTrue(accessTokenResponse.isPresent());
        return accessTokenResponse.get();
    }

    private QingPingAccessTokenData runParseAccessToken(String qingPingAccessTokenResponse) {
        final QingPingAccessTokenJsonParser parser = new QingPingAccessTokenJsonParser();
        final Optional<QingPingAccessTokenData> qingPingAccessToken = parser.parse(qingPingAccessTokenResponse);

        assertTrue(qingPingAccessToken.isPresent());
        return qingPingAccessToken.get();
    }

    private String runListDevicesRequest(QingPingAccessTokenData accessTokenData) throws URISyntaxException, CommunicationException, IOException {
        final String url = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(url);
        final QingPingListDevicesRequest listDevicesRequest = new QingPingListDevicesRequest(uri);

        return listDevicesRequest.sendRequest(accessTokenData.accessToken());
    }

    private QingPingSensorData runParseListDevices(String listDevicesResponse) {
        final QingPingListDevicesJsonParser parser = new QingPingListDevicesJsonParser();
        final Optional<QingPingSensorData> sensorData = parser.parseDeviceListResponse(listDevicesResponse, MAC_AIR_PRESSURE_DEVICE);

        assertTrue(sensorData.isPresent());
        return sensorData.get();
    }
}