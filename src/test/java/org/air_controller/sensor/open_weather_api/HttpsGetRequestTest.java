package org.air_controller.sensor.open_weather_api;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HttpsGetRequestTest {

    @Test
    void testWhenUrlCalledThenReturnContent() throws URISyntaxException {
        final String url = "https://example.com";
        final URI uri = new URI(url);
        final HttpsGetRequest testee = new HttpsGetRequest(uri);

        final Optional<String> result = testee.sendRequest();

        assertThat(result).isPresent();
        assertThat(result.get()).isNotEmpty();
    }

    @Test
    void testWhenUrlCalledTwiceThenReturnContentTwice() throws URISyntaxException {
        final String url = "https://example.com";
        final URI uri = new URI(url);
        final HttpsGetRequest testee = new HttpsGetRequest(uri);

        final Optional<String> result = testee.sendRequest();
        final Optional<String> result2 = testee.sendRequest();

        assertThat(result).isPresent();
        assertThat(result2).isPresent();
        assertThat(result.get()).isNotEmpty();
        assertThat(result2.get()).isNotEmpty();
    }
}