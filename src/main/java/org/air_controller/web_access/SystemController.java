package org.air_controller.web_access;

import org.air_controller.sensor_values.MeasuredValue;
import org.air_controller.web_access.graph.GraphView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class SystemController {

    private final AirControllerService service;

    public SystemController(AirControllerService service) {
        this.service = service;
    }

    @GetMapping("/graph/indoor/{measuredValue}/{hours}")
    public ResponseEntity<GraphView> getIndoorTemperatureGraph(@PathVariable MeasuredValue measuredValue, @PathVariable int hours) {
        final Duration duration = Duration.ofHours(hours);
        final GraphView graph = service.getIndoorGraphOfMeasuredValues(measuredValue, duration);
        return generateResponse(graph);
    }

    @GetMapping("/graph/outdoor/{measuredValue}/{hours}")
    public ResponseEntity<GraphView> getOutdoorTemperatureGraph(@PathVariable MeasuredValue measuredValue, @PathVariable int hours) {
        final Duration duration = Duration.ofHours(hours);
        final GraphView graph = service.getOutdoorGraphOfMeasuredValues(measuredValue, duration);
        return generateResponse(graph);
    }

    private ResponseEntity<GraphView> generateResponse(GraphView graph) {
        if (graph.items().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(graph, HttpStatus.OK);
    }
}
