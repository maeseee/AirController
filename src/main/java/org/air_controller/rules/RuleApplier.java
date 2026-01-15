package org.air_controller.rules;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.VentilationSystemPersistence;
import org.air_controller.system_action.VentilationSystemPersistenceData;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class RuleApplier implements Runnable {
    private final RuleApplierSystem airFlowSystem;
    private final RuleApplierSystem humidityExchangerSystem;

    public RuleApplier(VentilationSystem ventilationSystem, VentilationSystemPersistence ventilationSystemPersistence, List<Rule> freshAirRules,
            List<Rule> humidityExchangerRules) {
        final Consumer<OutputState> airFlowUpdateAction = ventilationSystem::setAirFlowOn;
        final Consumer<VentilationSystemPersistenceData> airFlowSystemPersistence = ventilationSystemPersistence::persistAirFlowData;
        this.airFlowSystem = new RuleApplierSystem(freshAirRules, airFlowUpdateAction, airFlowSystemPersistence);

        final Consumer<OutputState> humidityExchangerAction = ventilationSystem::setHumidityExchangerOn;
        final Consumer<VentilationSystemPersistenceData> humidityExchangerSystemPersistence = ventilationSystemPersistence::persistHumidityExchangerData;
        this.humidityExchangerSystem = new RuleApplierSystem(humidityExchangerRules, humidityExchangerAction, humidityExchangerSystemPersistence);
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
