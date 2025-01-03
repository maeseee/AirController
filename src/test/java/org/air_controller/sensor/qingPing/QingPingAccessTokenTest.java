package org.air_controller.sensor.qingPing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) class QingPingAccessTokenTest {

    @Mock
    private QingPingAccessTokenRequest request;

    @Test void shouldThrow_whenRequestFails() {
        when(request.sendRequest()).thenReturn(Optional.empty());
        final QingPingAccessToken testee = new QingPingAccessToken(request);

        assertThatExceptionOfType(CommunicationException.class).isThrownBy(testee::readToken);
    }
}