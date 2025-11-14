package org.air_controller.sensor.qing_ping;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
class ListDevicesRequestTest {

    @Test
    void shouldResponseToToken_whenSensingToQingPing() throws URISyntaxException, CommunicationException, IOException {
        final String url = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(url);
        final AccessToken accessToken = new AccessToken();
        final String token = accessToken.readToken();
        final ListDevicesRequest testee = new ListDevicesRequest(uri);

        final String response = testee.sendRequest(token);

        assertThat(response)
                .contains("data")
                .contains("product");
        Devices.getDeviceList().forEach(device -> assertThat(response).contains(device));
        assertThatNoException().isThrownBy(() -> testee.sendRequest(token));
    }

    @Test
    void shouldResponseToToken_whenSendingToAnything() throws URISyntaxException, CommunicationException, IOException {
        final String url = "https://httpbin.org/anything";
        final URI uri = new URI(url);
        final String accessToken = "accessToken";
        final ListDevicesRequest testee = new ListDevicesRequest(uri);

        final String response = testee.sendRequest(accessToken);

        final JSONTokener tokener = new JSONTokener(response);
        final JSONObject jsonObject = new JSONObject(tokener);
        final JSONObject args = jsonObject.getJSONObject("args");
        assertThat(args.has("timestamp")).isTrue();
        final JSONObject headers = jsonObject.getJSONObject("headers");
        final String authorization = headers.getString("Authorization");
        assertThat(authorization).isEqualTo("Bearer accessToken");
        final String contentType = headers.getString("Content-Type");
        assertThat(contentType).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void shouldThrow_whenInvalidUrl() throws URISyntaxException {
        final String url = "https://httpbinInvalid.org/anything";
        final URI uri = new URI(url);
        final String accessToken = "accessToken";
        final ListDevicesRequest testee = new ListDevicesRequest(uri);

        assertThatExceptionOfType(UnknownHostException.class).isThrownBy(() -> testee.sendRequest(accessToken));
    }
}