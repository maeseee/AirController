package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.secrets.Secret;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
        final AirValue airValue = runParseListDevices(listDevicesResponse);
        assertNotNull(airValue);
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
        final JsonQingPingParser parser = new JsonQingPingParser();
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

    private AirValue runParseListDevices(String listDevicesResponse) {
        final JsonQingPingParser parser = new JsonQingPingParser();
        final Optional<AirValue> airValue = parser.parseDeviceListResponse(listDevicesResponse, QingPingSensor.MAC_ADDRESSES);

        assertTrue(airValue.isPresent());
        return airValue.get();
    }
}