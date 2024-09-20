package org.airController.sensor.qingPing;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class QingPingListDevicesRequestTest {

    @Test
    void testWhenSendGetRequestThenReturnWithValues() throws URISyntaxException {
        final String url = "https://apis.cleargrass.com/v1/apis/devices";
        final URI uri = new URI(url);
        final QingPingListDevicesRequest testee = new QingPingListDevicesRequest(uri);

        final Optional<String> result = testee.sendRequest("S50MSCthuGnnLhAM86G6Rf3QnDtbPDdPotQLGpfVH1I.gPDGvBiBx_VK2HAm8j9pXoraDQguffQXKcSYUP-8jJ8");

        System.out.println(result);
    }

    @Test
    void testWhenSendGetRequestThenRequestIsInResponse() throws URISyntaxException {
        final String url = "https://httpbin.org/anything";
        final URI uri = new URI(url);
        final String accessToken = "accessToken";
        final QingPingListDevicesRequest testee = new QingPingListDevicesRequest(uri);

        final Optional<String> result = testee.sendRequest(accessToken);

        System.out.println(result);
        assertTrue(result.isPresent());
        final JSONTokener tokener = new JSONTokener(result.get());
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