package org.air_controller.gpio.dingtian_relay;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DingtianUrlCreatorTest {

    @Test
    void shouldCreateGetRelayStatesURL() {
        final DingtianUrlCreator testee = new DingtianUrlCreator();

        final String url = testee.createGetRelayStatesURL();

        assertThat(url).isEqualTo("http://192.168.50.22/relay_cgi_load.cgi");
    }

    @Test
    void shouldCreateSetRelayStateURL_whenSetOn() {
        final DingtianUrlCreator testee = new DingtianUrlCreator();

        final String url = testee.createSetRelayStateURL(1, DingtianAction.ON);

        assertThat(url).isEqualTo("http://192.168.50.22/relay_cgi.cgi?type=0&relay=1&on=1&time=0&pwd=0&");
    }

    @Test
    void shouldCreateSetRelayStateURL_whenSetOff() {
        final DingtianUrlCreator testee = new DingtianUrlCreator();

        final String url = testee.createSetRelayStateURL(2, DingtianAction.OFF);

        assertThat(url).isEqualTo("http://192.168.50.22/relay_cgi.cgi?type=0&relay=2&on=0&time=0&pwd=0&");
    }

    @Test
    void shouldThrow_whenInvalidRelayNumberGiven() {
        final DingtianUrlCreator testee = new DingtianUrlCreator();

        assertThatThrownBy(() -> testee.createSetRelayStateURL(5, DingtianAction.OFF))
                .isInstanceOf(IllegalArgumentException.class);
    }
}