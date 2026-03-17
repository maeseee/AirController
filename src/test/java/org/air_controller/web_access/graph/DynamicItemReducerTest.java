package org.air_controller.web_access.graph;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicItemReducerTest {

    @Test
    void shouldKeepSize_whenSmallerThenMax() {
        final DynamicItemReducer testee = new DynamicItemReducer(5);
        final LocalDateTime time = LocalDateTime.now();
        final List<GraphItem> list = List.of(
                new GraphItem(time, 1.0, "#4bc0c0"),
                new GraphItem(time, 1.2, "#4bc0c0"),
                new GraphItem(time, 1.5, "#4bc0c0")
        );

        final List<GraphItem> reducedItems = testee.reduce(list);

        assertThat(reducedItems).hasSize(3);
    }

}