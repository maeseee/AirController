package org.airController.controllers;

import org.airController.rules.Percentage;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreshAirControllerTest {

    @Mock
    private ControlledVentilationSystem ventilationSystem;

    @Mock
    private Rule rule;

    private final List<Rule> freshAirRules = new ArrayList<>();
    private final List<Rule> exchangeHumidityRules = new ArrayList<>();

    @Test
    void shouldTurnAirFlowOn_WhenPositivConfident() {
        when(rule.turnOn()).thenReturn(new Percentage(1.0));
        freshAirRules.add(rule);
        FreshAirController testee = new FreshAirController(ventilationSystem, freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(true);
        verify(ventilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void shouldNotTurnAirFlowOn_WhenNegativConfident() {
        when(rule.turnOn()).thenReturn(new Percentage(-1.0));
        freshAirRules.add(rule);
        FreshAirController testee = new FreshAirController(ventilationSystem, freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(false);
        verify(ventilationSystem).setHumidityExchangerOn(false);
    }

    @Test
    void shouldTurnHumidityExchangerOn_WhenPositivConfident() {
        when(rule.turnOn()).thenReturn(new Percentage(1.0));
        freshAirRules.add(rule);
        exchangeHumidityRules.add(rule);
        FreshAirController testee = new FreshAirController(ventilationSystem, freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(true);
        verify(ventilationSystem).setHumidityExchangerOn(true);
    }

    @Test
    void shouldNotTurnHumidityExchangerOn_WhenAirFlowOff() {
        when(rule.turnOn()).thenReturn(new Percentage(-1.0));
        freshAirRules.add(rule);
        Rule humidityRule = mock(Rule.class);
        when(humidityRule.turnOn()).thenReturn(new Percentage(1.0));
        exchangeHumidityRules.add(humidityRule);
        FreshAirController testee = new FreshAirController(ventilationSystem, freshAirRules, exchangeHumidityRules);

        testee.run();

        verify(ventilationSystem).setAirFlowOn(false);
        verify(ventilationSystem).setHumidityExchangerOn(false);
    }
}