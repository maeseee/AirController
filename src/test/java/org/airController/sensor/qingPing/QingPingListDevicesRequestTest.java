package org.airController.sensor.qingPing;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class QingPingListDevicesRequestTest {

    @Test
    void testWhenSendGetRequestThenReturnWithValues() throws URISyntaxException {
        final String url = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(url);
        final QingPingAccessToken accessToken = new QingPingAccessToken();
        final QingPingListDevicesRequest testee = new QingPingListDevicesRequest(uri);

        assertThatNoException().isThrownBy(() -> System.out.println(testee.sendRequest(accessToken.readToken())));
    }

    @Test
    void testWhenSendGetRequestThenRequestIsInResponse() throws URISyntaxException, CommunicationException, IOException {
        final String url = "https://httpbin.org/anything";
        final URI uri = new URI(url);
        final String accessToken = "accessToken";
        final QingPingListDevicesRequest testee = new QingPingListDevicesRequest(uri);

        final String result = testee.sendRequest(accessToken);

        System.out.println(result);
        final JSONTokener tokener = new JSONTokener(result);
        final JSONObject jsonObject = new JSONObject(tokener);
        final JSONObject args = jsonObject.getJSONObject("args");
        assertTrue(args.has("timestamp"));
        final JSONObject headers = jsonObject.getJSONObject("headers");
        final String authorization = headers.getString("Authorization");
        assertEquals("Bearer accessToken", authorization);
        final String contentType = headers.getString("Content-Type");
        assertEquals("application/x-www-form-urlencoded", contentType);
    }
}