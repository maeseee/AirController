package org.air_controller.sensor.qing_ping;

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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QingPingAccessTokenRequestTest {

    @Test
    void shouldHaveRequestInResponse_whenSendingPostRequest() throws URISyntaxException {
        final String credentials = "just a secret";
        final String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        final String url = "https://httpbin.org/anything";
        final URI uri = new URI(url);
        final QingPingAccessTokenRequest testee = new QingPingAccessTokenRequest(uri, base64Credentials);

        final Optional<String> result = testee.sendRequest();

        assertThat(result).isPresent();
        final JSONTokener tokener = new JSONTokener(result.get());
        final JSONObject jsonObject = new JSONObject(tokener);
        final JSONObject form = jsonObject.getJSONObject("form");
        final String grantType = form.getString("grant_type");
        assertThat(grantType).isEqualTo("client_credentials");
        final String scope = form.getString("scope");
        assertThat(scope).isEqualTo("device_full_access");
        final JSONObject headers = jsonObject.getJSONObject("headers");
        final String contentType = headers.getString("Content-Type");
        assertThat(contentType).isEqualTo("application/x-www-form-urlencoded");
    }
}