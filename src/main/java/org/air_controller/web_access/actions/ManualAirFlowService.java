package org.air_controller.web_access.actions;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.rules.ManualOverrideEvent;
import org.air_controller.system.OutputState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class ManualAirFlowService {

    private final ApplicationEventPublisher publisher;

    public ManualAirFlowService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void notifyManualAirFlowOverride(Duration duration, boolean switchOn) {
        publisher.publishEvent(new ManualOverrideEvent(ZonedDateTime.now(), OutputState.fromIsOnState(switchOn), duration));
    }
}
