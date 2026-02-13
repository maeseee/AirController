package org.air_controller.web_access;

import org.air_controller.sensor_values.MeasuredValue;
import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.web_access.graph.GraphView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Optional;

@RestController
public class SystemController {

    private final AirControllerService service;

    public SystemController(AirControllerService service) {
        this.service = service;
    }

    @GetMapping("/currentState/freshAir")
    public ResponseEntity<OutputState> getCurrentStateForFreshAir() {
        final Optional<OutputState> currentOutputState = service.getCurrentStateForFreshAir()
                .map(SystemAction::outputState);
        return generateResponse(currentOutputState);
    }

    @GetMapping("/graph/indoor/{measuredValue}/{hours}")
    public ResponseEntity<GraphView> getIndoorTemperatureGraph(@PathVariable MeasuredValue measuredValue, @PathVariable int hours) {
        final Duration duration = Duration.ofHours(hours);
        final GraphView graph = service.getIndoorGraphOfMeasuredValue(measuredValue, duration);
        return generateResponse(graph);
    }

    private <T> ResponseEntity<T> generateResponse(Optional<T> resultOptional) {
        return resultOptional.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ResponseEntity<GraphView> generateResponse(GraphView graph) {
        if (graph.items().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(graph, HttpStatus.OK);
    }
}
