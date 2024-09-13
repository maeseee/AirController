package org.airController.controllers;

import org.airController.systemAdapter.ControlledVentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class FreshAirController implements Runnable {
    private static final Logger logger = LogManager.getLogger(FreshAirController.class);

    private final ControlledVentilationSystem ventilationSystem;
    private final List<Rule> freshAirRules;
    private final List<Rule> exchangeHumidityRules;

    public FreshAirController(ControlledVentilationSystem ventilationSystem, List<Rule> freshAirRules, List<Rule> exchangeHumidityRules) {
        this.ventilationSystem = ventilationSystem;
        this.freshAirRules = freshAirRules;
        this.exchangeHumidityRules = exchangeHumidityRules;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in AirController loop:", exception);
        }
    }

    private void doRun() {
        //sensorValues.invalidateSensorValuesIfNeeded();
        double confidentForFreshAir = freshAirRules.stream()
                .mapToDouble(rule -> rule.turnOn().getPercentage())
                .sum();
        boolean freshAirOn = confidentForFreshAir > 0;
        double confidentHumidityExchange = exchangeHumidityRules.stream()
                .mapToDouble(rule -> rule.turnOn().getPercentage())
                .sum();
        boolean exchangeHumidityOn = confidentHumidityExchange > 0;

        final boolean stateChanged = ventilationSystem.setAirFlowOn(freshAirOn);
        ventilationSystem.setHumidityExchangerOn(exchangeHumidityOn && freshAirOn);

        if (stateChanged) {
            String freshAirPercentage = freshAirRules.stream()
                    .map(rule -> rule.name() + ": " + rule.turnOn().getPercentage() + ", ")
                    .collect(Collectors.joining());
            logger.info("Fresh air is on because of {}", freshAirPercentage);
        }
    }
}
