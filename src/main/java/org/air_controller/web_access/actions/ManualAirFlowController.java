package org.air_controller.web_access.actions;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ManualAirFlowController {

    private final ManualAirFlowService service;

    public ManualAirFlowController(ManualAirFlowService service) {
        this.service = service;
    }

    @PostMapping("/action/airflow/{state}/{minutes}")
    public void setAirflowState(@PathVariable String state, @PathVariable int minutes) {
        final Duration duration = Duration.ofMinutes(minutes);
        final boolean on = "on".equalsIgnoreCase(state);
        service.notifyManualAirFlowOverride(duration, on);
    }
}
