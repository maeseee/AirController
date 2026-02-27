package org.air_controller.rules;

import org.air_controller.system.OutputState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
class ManualOverrideAirFlow implements AirFlowRule {

    private ZonedDateTime validUntil = ZonedDateTime.now(ZoneOffset.UTC);
    private OutputState givenState = OutputState.OFF;

    @Override
    public String name() {
        return "Overridden Air Flow";
    }

    @Override
    public Confidence turnOnConfidence() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (validUntil.isBefore(now)) {
            return Confidence.createEmpty();
        }
        final int stateSign = givenState.isOn() ? 1 : -1;
        return Confidence.createWeighted(stateSign, 10);
    }

    @EventListener
    public void manualOverride(ManualOverrideEvent event) {
        validUntil = event.getTimestamp().plus(event.getDuration());
        givenState = event.getAction();
    }
}
