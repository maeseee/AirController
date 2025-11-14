package org.air_controller.gpio.dingtian_relay;

import org.air_controller.http.HttpsGetRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
class RelayCommunicationTest {
    @Mock
    private HttpsGetRequest getRequest;
    @Mock
    private ResponseInterpreter interpreter;

    @Test
    void shouldReadRelayStates() {
        final RelayCommunication testee = new RelayCommunication();

        final List<Boolean> relayStates = testee.readStates();

        assertThat(relayStates).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyList_whenResponseEmpty() {
        when(getRequest.sendRequest(any())).thenReturn("");
        final RelayCommunication testee = new RelayCommunication(getRequest, interpreter);

        final List<Boolean> relayStates = testee.readStates();

        assertThat(relayStates).isEmpty();
        verify(getRequest).sendRequest("http://192.168.50.22/relay_cgi_load.cgi");
        verifyNoInteractions(interpreter);
    }

    @Test
    void shouldInterpretResponse_whenValid() {
        when(getRequest.sendRequest(any())).thenReturn("RESPONSE");
        final RelayCommunication testee = new RelayCommunication(getRequest, interpreter);

        final List<Boolean> relayStates = testee.readStates();

        assertThat(relayStates).isEmpty();
        verify(getRequest).sendRequest("http://192.168.50.22/relay_cgi_load.cgi");
        verify(interpreter).interpretRelayState("RESPONSE");
    }

    @Test
    void shouldSetRelayState() {
        when(getRequest.sendRequest(any())).thenReturn("RESPONSE");
        final RelayCommunication testee = new RelayCommunication(getRequest, interpreter);

        testee.setRelayState(0, true);

        verify(getRequest).sendRequest("http://192.168.50.22/relay_cgi.cgi?type=0&relay=0&on=1&time=0&pwd=0&");
    }
}