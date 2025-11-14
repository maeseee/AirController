package org.air_controller.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpsGetRequestTest {

    @Test
    void testWhenUrlCalledThenReturnContent() {
        final String url = "https://example.com";
        final HttpsGetRequest testee = new HttpsGetRequest();

        final String result = testee.sendRequest(url);

        assertThat(result).isNotEmpty();
    }

    @Test
    void testWhenUrlCalledTwiceThenReturnContentTwice() {
        final String url = "https://example.com";
        final HttpsGetRequest testee = new HttpsGetRequest();

        final String result = testee.sendRequest(url);
        final String result2 = testee.sendRequest(url);

        assertThat(result).isNotEmpty();
        assertThat(result2).isNotEmpty();
    }
}