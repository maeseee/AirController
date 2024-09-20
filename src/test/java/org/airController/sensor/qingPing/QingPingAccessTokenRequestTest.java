package org.airController.sensor.qingPing;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class QingPingAccessTokenRequestTest {

    @Test
    void testWhenSendPostRequestThenRequestIsInResponse() throws URISyntaxException {
        final String credentials = "just a secret";
        final String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        final String url = "https://httpbin.org/anything";
        final URI uri = new URI(url);
        final QingPingAccessTokenRequest testee = new QingPingAccessTokenRequest(uri, base64Credentials);

        final Optional<String> result = testee.sendRequest();

        assertTrue(result.isPresent());
        final JSONTokener tokener = new JSONTokener(result.get());
        final JSONObject jsonObject = new JSONObject(tokener);
        final JSONObject form = jsonObject.getJSONObject("form");
        final String grantType = form.getString("grant_type");
        assertEquals("client_credentials", grantType);
        final String scope = form.getString("scope");
        assertEquals("device_full_access", scope);
        final JSONObject headers = jsonObject.getJSONObject("headers");
        final String contentType = headers.getString("Content-Type");
        assertEquals("application/x-www-form-urlencoded", contentType);
    }
}