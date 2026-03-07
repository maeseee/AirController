package org.air_controller.rules.airflow;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system.OutputState;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class RuleApplier {
    private final RuleApplierSystem airFlowSystem;
    private final RuleApplierSystem humidityExchangerSystem;

    public RuleApplier(
            VentilationSystem ventilationSystem,
            @Qualifier("airFlowAccessor") SystemActionDbAccessor airFlow,
            @Qualifier("humidityAccessor") SystemActionDbAccessor humidity,
            List<AirFlowRule> airFlowRules,
            List<HumidityExchangeRule> humidityExchangeRules) {
        final Consumer<OutputState> airFlowUpdateAction = ventilationSystem::setAirFlowOn;
        final Consumer<VentilationSystemPersistenceData> airFlowSystemPersistence = airFlow::insertAction;
        this.airFlowSystem = new RuleApplierSystem(airFlowRules, airFlowUpdateAction, airFlowSystemPersistence);

        final Consumer<OutputState> humidityExchangerAction = ventilationSystem::setHumidityExchangerOn;
        final Consumer<VentilationSystemPersistenceData> humidityExchangerSystemPersistence = humidity::insertAction;
        this.humidityExchangerSystem = new RuleApplierSystem(humidityExchangeRules, humidityExchangerAction, humidityExchangerSystemPersistence);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void runEveryMinute() {
        try {
            doRun();
        } catch (Exception exception) {
            log.error("Exception in RuleApplier loop:", exception);
        }
    }

    private void doRun() {
        airFlowSystem.updateSystemIfNecessary(true);
        humidityExchangerSystem.updateSystemIfNecessary(airFlowSystem.getCurrentState().isOn());
    }
}
