package org.air_controller.rules;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class RuleApplier implements Runnable {
    private final RuleApplierSystem airFlowSystem;
    private final RuleApplierSystem humidityExchangerSystem;

    public RuleApplier(VentilationSystem ventilationSystem, SystemActionDbAccessor airFlow, SystemActionDbAccessor humidity, List<AirFlowRule> airFlowRules,
            List<HumidityExchangeRule> humidityExchangeRules) {
        final Consumer<OutputState> airFlowUpdateAction = ventilationSystem::setAirFlowOn;
        final Consumer<VentilationSystemPersistenceData> airFlowSystemPersistence = airFlow::insertAction;
        this.airFlowSystem = new RuleApplierSystem(airFlowRules, airFlowUpdateAction, airFlowSystemPersistence);

        final Consumer<OutputState> humidityExchangerAction = ventilationSystem::setHumidityExchangerOn;
        final Consumer<VentilationSystemPersistenceData> humidityExchangerSystemPersistence = humidity::insertAction;
        this.humidityExchangerSystem = new RuleApplierSystem(humidityExchangeRules, humidityExchangerAction, humidityExchangerSystemPersistence);
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            log.error("Exception in AirController loop:", exception);
        }
    }

    private void doRun() {
        airFlowSystem.updateSystemIfNecessary(true);
        humidityExchangerSystem.updateSystemIfNecessary(airFlowSystem.getCurrentState().isOn());
    }
}
