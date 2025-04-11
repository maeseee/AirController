package org.air_controller.http;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HttpsGetRequestTest {

    @Test
    void testWhenUrlCalledThenReturnContent() {
        final String url = "https://example.com";
        final HttpsGetRequest testee = new HttpsGetRequest();

        final Optional<String> result = testee.sendRequest(url);

        assertThat(result).isPresent();
        assertThat(result.get()).isNotEmpty();
    }

    @Test
    void testWhenUrlCalledTwiceThenReturnContentTwice() {
        final String url = "https://example.com";
        final HttpsGetRequest testee = new HttpsGetRequest();

        final Optional<String> result = testee.sendRequest(url);
        final Optional<String> result2 = testee.sendRequest(url);

        assertThat(result).isPresent();
        assertThat(result2).isPresent();
        assertThat(result.get()).isNotEmpty();
        assertThat(result2.get()).isNotEmpty();
    }
}