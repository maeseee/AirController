package org.airController.sensor.qingPing;

import org.airController.secrets.Secret;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class QingPingIntegrationTest {

    @Test
    void testQingPingDevice() throws URISyntaxException {
        final String accessTokenResponse = runAccessTokenRequest();
        final QingPingAccessToken accessToken = runParseAccessToken(accessTokenResponse);
        final String listDevicesResponse = runListDevicesRequest(accessToken);
        final QingPingSensorData airValue = runParseListDevices(listDevicesResponse);
        assertNotNull(airValue);
        assertTrue(LocalDateTime.now().minusMinutes(15).isBefore(airValue.getTimeStamp()));
        assertTrue(LocalDateTime.now().plusMinutes(5).isAfter(airValue.getTimeStamp()));
    }

    private String runAccessTokenRequest() throws URISyntaxException {
        final String appKey = QingPingSensor.APP_KEY;
        final String appSecret = Secret.getSecret(QingPingSensor.ENVIRONMENT_VARIABLE_APP_SECRET, QingPingSensor.ENCRYPTED_APP_SECRET);
        final String credentials = appKey + ":" + appSecret;
        final String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        final String url = "https://oauth.cleargrass.com/oauth2/token";
        final URI uri = new URI(url);
        final QingPingAccessTokenRequest accessTokenRequest = new QingPingAccessTokenRequest(uri, base64Credentials);

        final Optional<String> accessTokenResponse = accessTokenRequest.sendRequest();

        assertTrue(accessTokenResponse.isPresent());
        return accessTokenResponse.get();
    }

    private QingPingAccessToken runParseAccessToken(String qingPingAccessTokenResponse) {
        final QingPingJsonParser parser = new QingPingJsonParser();
        final Optional<QingPingAccessToken> qingPingAccessToken = parser.parseAccessTokenResponse(qingPingAccessTokenResponse);

        assertTrue(qingPingAccessToken.isPresent());
        return qingPingAccessToken.get();
    }

    private String runListDevicesRequest(QingPingAccessToken accessToken) throws URISyntaxException {
        final String url = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(url);
        final QingPingListDevicesRequest listDevicesRequest = new QingPingListDevicesRequest(uri);

        final Optional<String> listDevicesResponse = listDevicesRequest.sendRequest(accessToken.accessToken());

        assertTrue(listDevicesResponse.isPresent());
        return listDevicesResponse.get();
    }

    private QingPingSensorData runParseListDevices(String listDevicesResponse) {
        final QingPingJsonParser parser = new QingPingJsonParser();
        final Optional<QingPingSensorData> sensorData = parser.parseDeviceListResponse(listDevicesResponse, QingPingSensor.MAC_PRESSURE_DEVICE);

        assertTrue(sensorData.isPresent());
        return sensorData.get();
    }
}