package org.air_controller.rules;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class ManualOverrideAirFlow implements Rule {

    @Override
    public String name() {
        return "Manual Override Air Flow";
    }

    @Override
    public Confidence turnOnConfidence() {
        return Confidence.createEmpty();
    }

    @EventListener
    @Async
    public void manualOverride(ManualOverrideEvent event) {
        // TODO
    }

}
