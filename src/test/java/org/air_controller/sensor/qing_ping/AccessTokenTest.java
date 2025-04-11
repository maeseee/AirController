package org.air_controller.sensor.qing_ping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenTest {

    @Mock
    private AccessTokenRequest request;

    @Test
    void shouldThrow_whenRequestFails() {
        when(request.sendRequest()).thenReturn(Optional.empty());
        final AccessToken testee = new AccessToken(request);

        assertThatExceptionOfType(CommunicationException.class).isThrownBy(testee::readToken);
    }
}