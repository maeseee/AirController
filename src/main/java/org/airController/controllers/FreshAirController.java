package org.airController.controllers;

import org.airController.systemAdapter.ControlledVentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class FreshAirController implements Runnable {
    private static final Logger logger = LogManager.getLogger(FreshAirController.class);

    private final List<ControlledVentilationSystem> ventilationSystem;
    private final List<Rule> freshAirRules;
    private final List<Rule> exchangeHumidityRules;
    private boolean airFlowStateOn = true;
    private boolean humidityExchangerStateOn = false;

    public FreshAirController(List<ControlledVentilationSystem> ventilationSystem, List<Rule> freshAirRules, List<Rule> exchangeHumidityRules) {
        this.ventilationSystem = ventilationSystem;
        this.freshAirRules = freshAirRules;
        this.exchangeHumidityRules = exchangeHumidityRules;
        ventilationSystem.forEach(system -> system.setAirFlowOn(airFlowStateOn));
        ventilationSystem.forEach(system -> system.setHumidityExchangerOn(humidityExchangerStateOn));
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
        boolean airFlowOn = confidentForFreshAir > 0;
        updateAirFlow(airFlowOn);

        double confidentHumidityExchange = exchangeHumidityRules.stream()
                .mapToDouble(rule -> rule.turnOn().getPercentage())
                .sum();
        boolean humidityExchangerOn = confidentHumidityExchange > 0 && airFlowOn;
        updateHumidityExchanger(humidityExchangerOn);

    }

    private void updateAirFlow(boolean airFlowOn) {
        if (airFlowStateOn != airFlowOn) {
            ventilationSystem.forEach(system -> system.setAirFlowOn(airFlowOn));
            airFlowStateOn = airFlowOn;
            String freshAirPercentage = freshAirRules.stream()
                    .map(rule -> rule.name() + ": " + rule.turnOn().getPercentage() + ", ")
                    .collect(Collectors.joining());
            logger.info("Fresh air is on because of {}", freshAirPercentage);
        }
    }

    private void updateHumidityExchanger(boolean humidityExchangerOn) {
        if (humidityExchangerStateOn != humidityExchangerOn) {
            ventilationSystem.forEach(system -> system.setHumidityExchangerOn(humidityExchangerOn));
            humidityExchangerStateOn = humidityExchangerOn;
        }
    }
}
