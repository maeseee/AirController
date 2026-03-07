package org.air_controller.controllers;

import org.air_controller.rules.Confidence;
import org.air_controller.rules.airflow.AirFlowRule;
import org.air_controller.rules.airflow.HumidityExchangeRule;
import org.air_controller.rules.airflow.RuleApplier;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleApplierTest {

    private static final double CONFIDENCE_WEIGHT = 1.0;

    @Mock
    private VentilationSystem ventilationSystem;
    @Mock
    private SystemActionDbAccessor airFlowDbAccessor;
    @Mock
    private SystemActionDbAccessor humidityDbAccessor;
    @Mock
    private AirFlowRule airFlowRule;
    @Mock
    private HumidityExchangeRule humidityExchangeRule;

    private final List<AirFlowRule> airFlowRules = new ArrayList<>();
    private final List<HumidityExchangeRule> humidityExchangeRules = new ArrayList<>();

    @Test
    void shouldTurnAirFlowOn_whenPositivConfidence() {
        when(airFlowRule.turnOnConfidence()).thenReturn(Confidence.createWeighted(1.0, CONFIDENCE_WEIGHT));
        airFlowRules.add(airFlowRule);
        final RuleApplier testee = new RuleApplier(ventilationSystem, airFlowDbAccessor, humidityDbAccessor, airFlowRules, humidityExchangeRules);

        testee.runEveryMinute();

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(airFlowDbAccessor).insertAction(any());
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
        verify(humidityDbAccessor).insertAction(any());
    }

    @Test
    void shouldNotTurnAirFlowOn_whenNegativConfidence() {
        when(airFlowRule.turnOnConfidence()).thenReturn(Confidence.createWeighted(-1.0, CONFIDENCE_WEIGHT));
        airFlowRules.add(airFlowRule);
        final RuleApplier testee = new RuleApplier(ventilationSystem, airFlowDbAccessor, humidityDbAccessor, airFlowRules, humidityExchangeRules);

        testee.runEveryMinute();

        verify(ventilationSystem).setAirFlowOn(OutputState.OFF);
        verify(airFlowDbAccessor).insertAction(any());
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
        verify(humidityDbAccessor).insertAction(any());
    }

    @Test
    void shouldTurnHumidityExchangerOn_whenPositivConfidenceForHumidityAndAirFlow() {
        when(airFlowRule.turnOnConfidence()).thenReturn(Confidence.createWeighted(1.0, CONFIDENCE_WEIGHT));
        airFlowRules.add(airFlowRule);
        when(humidityExchangeRule.turnOnConfidence()).thenReturn(Confidence.createWeighted(0.7, CONFIDENCE_WEIGHT));
        humidityExchangeRules.add(humidityExchangeRule);
        final RuleApplier testee = new RuleApplier(ventilationSystem, airFlowDbAccessor, humidityDbAccessor, airFlowRules, humidityExchangeRules);

        testee.runEveryMinute();

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(airFlowDbAccessor).insertAction(any());
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.ON);
        verify(humidityDbAccessor).insertAction(any());
    }

    @Test
    void shouldNotTurnHumidityExchangerOn_whenAirFlowOff() {
        when(airFlowRule.turnOnConfidence()).thenReturn(Confidence.createWeighted(-1.0, CONFIDENCE_WEIGHT));
        airFlowRules.add(airFlowRule);
        humidityExchangeRules.add(humidityExchangeRule);
        final RuleApplier testee = new RuleApplier(ventilationSystem, airFlowDbAccessor, humidityDbAccessor, airFlowRules, humidityExchangeRules);

        testee.runEveryMinute();

        verify(ventilationSystem).setAirFlowOn(OutputState.OFF);
        verify(airFlowDbAccessor).insertAction(any());
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
        verify(humidityDbAccessor).insertAction(any());
    }

    @Test
    void shouldNotTurnOff_whenAirFlowInHysteresis() {
        when(airFlowRule.turnOnConfidence())
                .thenReturn(Confidence.createWeighted(1.0, CONFIDENCE_WEIGHT)) // on
                .thenReturn(Confidence.createWeighted(-0.04, CONFIDENCE_WEIGHT)); // in hysteresis
        airFlowRules.add(airFlowRule);
        final RuleApplier testee = new RuleApplier(ventilationSystem, airFlowDbAccessor, humidityDbAccessor, airFlowRules, humidityExchangeRules);

        testee.runEveryMinute(); // on
        testee.runEveryMinute(); // nothing

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(airFlowDbAccessor, times(2)).insertAction(any());
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
        verify(humidityDbAccessor, times(2)).insertAction(any());
    }

    @Test
    void shouldTurnOff_whenAirFlowOutOfHysteresis() {
        when(airFlowRule.turnOnConfidence())
                .thenReturn(Confidence.createWeighted(1.0, CONFIDENCE_WEIGHT)) // on
                .thenReturn(Confidence.createWeighted(-0.075, CONFIDENCE_WEIGHT)); // out of hysteresis
        airFlowRules.add(airFlowRule);
        final RuleApplier testee = new RuleApplier(ventilationSystem, airFlowDbAccessor, humidityDbAccessor, airFlowRules, humidityExchangeRules);

        testee.runEveryMinute(); // on
        testee.runEveryMinute(); // off

        verify(ventilationSystem).setAirFlowOn(OutputState.ON);
        verify(ventilationSystem).setAirFlowOn(OutputState.OFF);
        verify(airFlowDbAccessor, times(2)).insertAction(any());
        verify(ventilationSystem).setHumidityExchangerOn(OutputState.OFF);
        verify(humidityDbAccessor, times(2)).insertAction(any());
    }
}