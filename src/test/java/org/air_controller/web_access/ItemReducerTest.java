package org.air_controller.web_access;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemReducerTest {

    @Test
    void shouldReturnOriginalData_whenMaxHigher() {
        final List<Integer> items = IntStream.rangeClosed(1, 100)
                .boxed()
                .toList();

        final List<Integer> integers = ItemReducer.reduceTo(items, 150);

        assertThat(integers).isEqualTo(items);
    }

    @Test
    void shouldReturnEvery4ItemAndConsistTheLastOne_whenMax4TimesSmaller() {
        final List<Integer> items = IntStream.rangeClosed(1, 100)
                .boxed()
                .toList();

        final List<Integer> integers = ItemReducer.reduceTo(items, 25);

        assertThat(integers.size()).isEqualTo(25);
        assertThat(integers.getLast()).isEqualTo(100);
    }

    @Test
    void shouldReturnEvery4ItemAndConsistTheLastOne_whenNotAMultiplesOfOf4() {
        final List<Integer> items = IntStream.rangeClosed(1, 98)
                .boxed()
                .toList();

        final List<Integer> integers = ItemReducer.reduceTo(items, 25);

        assertThat(integers.size()).isEqualTo(25);
        assertThat(integers.getLast()).isEqualTo(98);
    }
}