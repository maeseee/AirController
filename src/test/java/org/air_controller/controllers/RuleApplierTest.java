package org.air_controller.controllers;

import org.air_controller.rules.Confidence;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleApplierTest {

    @Mock
    private VentilationSystem ventilationSystem;

    @Mock
    private Rule rule;

    private final List<Rule> freshAirRules = new ArrayList<>();
    private final List<Rule> exchangeHumidityRules = new ArrayList<>();
    private static final double CONFIDENCE_WEIGHT = 1.0;

    @Test
    void shouldTurnAirFlowOn_whenPositivConfidence() {
        when(rule.turnOnConfidence()).thenReturn(new Confidence(1.0, CONFIDENCE_WEIGHT));
        freshAirRules.add(rule);
        final RuleApplier testee = new RuleApplier(singletonList(ventilationSystem), freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
    }

    @Test
    void shouldNotTurnAirFlowOn_whenNegativConfidence() {
        when(rule.turnOnConfidence()).thenReturn(new Confidence(-1.0, CONFIDENCE_WEIGHT));
        freshAirRules.add(rule);
        final RuleApplier testee = new RuleApplier(singletonList(ventilationSystem), freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(OutputState.OFF);
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
    }

    @Test
    void shouldTurnHumidityExchangerOn_whenPositivConfidenceForHumidityAndAirFlow() {
        when(rule.turnOnConfidence()).thenReturn(new Confidence(1.0, CONFIDENCE_WEIGHT));
        freshAirRules.add(rule);
        exchangeHumidityRules.add(rule);
        final RuleApplier testee = new RuleApplier(singletonList(ventilationSystem), freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.ON);
    }

    @Test
    void shouldNotTurnHumidityExchangerOn_whenAirFlowOff() {
        when(rule.turnOnConfidence()).thenReturn(new Confidence(-1.0, CONFIDENCE_WEIGHT));
        freshAirRules.add(rule);
        final Rule humidityRule = mock(Rule.class);
        exchangeHumidityRules.add(humidityRule);
        final RuleApplier testee = new RuleApplier(singletonList(ventilationSystem), freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(OutputState.OFF);
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
    }

    @Test
    void shouldNotTurnOff_whenAirFlowInHysteresis() {
        when(rule.turnOnConfidence())
                .thenReturn(new Confidence(1.0, CONFIDENCE_WEIGHT)) // on
                .thenReturn(new Confidence(-0.04, CONFIDENCE_WEIGHT)); // in hysteresis
        freshAirRules.add(rule);
        final RuleApplier testee = new RuleApplier(singletonList(ventilationSystem), freshAirRules, exchangeHumidityRules);

        testee.run(); // on
        testee.run(); // nothing

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
    }

    @Test
    void shouldTurnOff_whenAirFlowOutOfHysteresis() {
        when(rule.turnOnConfidence())
                .thenReturn(new Confidence(1.0, CONFIDENCE_WEIGHT)) // on
                .thenReturn(new Confidence(-0.06, CONFIDENCE_WEIGHT)); // out of hysteresis
        freshAirRules.add(rule);
        final RuleApplier testee = new RuleApplier(singletonList(ventilationSystem), freshAirRules, exchangeHumidityRules);

        testee.run(); // on
        testee.run(); // off

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(ventilationSystem).setAirFlowOn(OutputState.OFF);
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
    }
}