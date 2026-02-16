package org.air_controller.web_access.graph;

import org.air_controller.sensor_values.MeasuredValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class OutdoorSensorGraphController extends SensorGraphService{

    private final OutdoorSensorGraphService service;

    public OutdoorSensorGraphController(OutdoorSensorGraphService service) {
        this.service = service;
    }

    @GetMapping("/graph/outdoor/{measuredValue}/{hours}")
    public ResponseEntity<GraphView> getOutdoorTemperatureGraph(@PathVariable MeasuredValue measuredValue, @PathVariable int hours) {
        final Duration duration = Duration.ofHours(hours);
        final GraphView graph = service.getOutdoorGraphOfMeasuredValues(measuredValue, duration);
        return generateResponse(graph);
    }
}
