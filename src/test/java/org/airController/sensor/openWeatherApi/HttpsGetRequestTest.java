package org.airController.sensor.openWeatherApi;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HttpsGetRequestTest {

    @Test
    void testWhenUrlCalledThenReturnContent() throws URISyntaxException {
        final String url = "https://example.com";
        final URI uri = new URI(url);
        final HttpsGetRequest testee = new HttpsGetRequest(uri);

        final Optional<String> result = testee.sendRequest();

        assertThat(result.isPresent(), is(true));
        assertFalse(result.get().isEmpty());
    }

    @Test
    void testWhenUrlCalledTwiceThenReturnContentTwice() throws URISyntaxException {
        final String url = "https://example.com";
        final URI uri = new URI(url);
        final HttpsGetRequest testee = new HttpsGetRequest(uri);

        final Optional<String> result = testee.sendRequest();
        final Optional<String> result2 = testee.sendRequest();

        assertThat(result.isPresent(), is(true));
        assertThat(result2.isPresent(), is(true));
        assertFalse(result.get().isEmpty());
        assertFalse(result2.get().isEmpty());
    }
}