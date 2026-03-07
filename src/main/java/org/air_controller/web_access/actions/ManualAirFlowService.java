package org.air_controller.web_access.actions;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.rules.airflow.ManualOverrideEvent;
import org.air_controller.system.OutputState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
@Slf4j
class ManualAirFlowService {

    private final ApplicationEventPublisher publisher;

    public ManualAirFlowService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void notifyManualAirFlowOverride(Duration duration, boolean switchOn) {
        log.info("ManualAirFlow triggered {} for {} minutes", switchOn, duration.toMinutes());
        publisher.publishEvent(new ManualOverrideEvent(ZonedDateTime.now(ZoneOffset.UTC), OutputState.fromIsOnState(switchOn), duration));
    }
}
