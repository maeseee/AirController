package org.air_controller.web_access.card_view;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemCardViewServiceTest {

    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;

    @Test
    void shouldGetOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actions = List.of(
                new SystemAction(now.minusHours(10), OutputState.ON),
                new SystemAction(now.minusHours(9), OutputState.OFF)
        );
        when(airFlowDbAccessor.getActions(any())).thenReturn(actions);
        final StatisticsCardViewService testee = new StatisticsCardViewService(airFlowDbAccessor);

        final CardView statistics = testee.getCardView();

        assertThat(statistics.info()).isEmpty();
        assertThat(statistics.cards()).hasSize(1);
        assertThat(statistics.cards().getFirst().name()).contains("24h");
        assertThat(statistics.cards().getFirst().value()).isEqualTo("4.17"); // 1/24 * 100
        assertThat(statistics.cards().getFirst().unit()).isEqualTo("%");
    }

    @Test
    void shouldOnlyGetOnPercentageFromTheLast24Hours() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final List<SystemAction> actions = List.of(
                new SystemAction(now.minusHours(25), OutputState.ON),
                new SystemAction(now.minusHours(22), OutputState.OFF)
        );
        when(airFlowDbAccessor.getActions(any())).thenReturn(actions);
        final StatisticsCardViewService testee = new StatisticsCardViewService(airFlowDbAccessor);

        final CardView statistics = testee.getCardView();

        assertThat(statistics.info()).isEmpty();
        assertThat(statistics.cards()).hasSize(1);
        assertThat(statistics.cards().getFirst().name()).contains("24h");
        assertThat(statistics.cards().getFirst().value()).isEqualTo("8.33"); // 2/24 * 100
        assertThat(statistics.cards().getFirst().unit()).isEqualTo("%");
    }

    @Test
    void shouldReturnConfidenceCards() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final Map<String, Double> confidences = Map.of("MyTest", 0.5);
        final VentilationSystemPersistenceData data = new VentilationSystemPersistenceData(OutputState.ON, 1.0, confidences, now);
        when(airFlowDbAccessor.getMostCurrentPersistenceData()).thenReturn(Optional.of(data));
        final ConfidenceCardViewService testee = new ConfidenceCardViewService(airFlowDbAccessor);

        final CardView confidenceCardView = testee.getCardView();

        assertThat(confidenceCardView.info()).isEqualTo("Total confidence of 1.00");
        assertThat(confidenceCardView.cards()).hasSize(1);
        assertThat(confidenceCardView.cards().getFirst().name()).isEqualTo("MyTest");
        assertThat(confidenceCardView.cards().getFirst().value()).isEqualTo("0.50");
        assertThat(confidenceCardView.cards().getFirst().unit()).isEqualTo("");
    }
}