package org.air_controller.gpio.dingtian_relay;

import org.air_controller.http.HttpsGetRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DingtianRelayTest {
    @Mock
    private HttpsGetRequest getRequest;
    @Mock
    private ResponseInterpreter interpreter;

    @Test
    void shouldReadRelayStates() {
        final DingtianRelay testee = new DingtianRelay();

        final List<Boolean> relayStates = testee.readStates();

        assertThat(relayStates).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenResponseEmpty() {
        when(getRequest.sendRequest(any())).thenReturn(Optional.empty());
        final DingtianRelay testee = new DingtianRelay(getRequest, interpreter);

        final List<Boolean> relayStates = testee.readStates();

        assertThat(relayStates).isEmpty();
        verify(getRequest).sendRequest("http://192.168.50.22/relay_cgi_load.cgi");
        verifyNoInteractions(interpreter);
    }

    @Test
    void shouldInterpretResponse_whenValid() {
        when(getRequest.sendRequest(any())).thenReturn(Optional.of("RESPONSE"));
        final DingtianRelay testee = new DingtianRelay(getRequest, interpreter);

        final List<Boolean> relayStates = testee.readStates();

        assertThat(relayStates).isEmpty();
        verify(getRequest).sendRequest("http://192.168.50.22/relay_cgi_load.cgi");
        verify(interpreter).interpretRelayState("RESPONSE");
    }

    @Test
    void shouldSetRelayState() {
        when(getRequest.sendRequest(any())).thenReturn(Optional.of("RESPONSE"));
        final DingtianRelay testee = new DingtianRelay(getRequest, interpreter);

        testee.setRelayState(0, true);

        verify(getRequest).sendRequest("http://192.168.50.22/relay_cgi.cgi?type=0&relay=0&on=1&time=0&pwd=0&");
    }
}