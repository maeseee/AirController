package org.airController.sensorValues;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CarbonDioxideTest {

    @Test
    void shouldThrow_whenValueBelow0() {
        assertThatExceptionOfType(InvalidArgumentException.class).isThrownBy(() -> CarbonDioxide.createFromPpm(-1.0));
    }

    @Test
    void shouldThrow_whenValueAboveOneMillion() {
        assertThatExceptionOfType(InvalidArgumentException.class).isThrownBy(() -> CarbonDioxide.createFromPpm(1000001.0));
    }

    @Test
    void shouldReturnInitializedValue() throws InvalidArgumentException {
        CarbonDioxide testee = CarbonDioxide.createFromPpm(123456.0);

        double co2 = testee.getPpm();

        assertThat(co2).isEqualTo(123456.0);
    }

    @Test
    void shouldCompareValueOnEqualsTo() throws InvalidArgumentException {
        CarbonDioxide carbonDioxide1 = CarbonDioxide.createFromPpm(123456.0);
        CarbonDioxide carbonDioxide2 = CarbonDioxide.createFromPpm(123456.0);

        assertThat(carbonDioxide1).isEqualTo(carbonDioxide2);
        assertThat(carbonDioxide2).isEqualTo(carbonDioxide1);
    }
}