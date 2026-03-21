package org.air_controller.web_access.graph;

import org.air_controller.sensor_values.MeasuredValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/graph/outdoor")
public class OutdoorSensorGraphController extends SensorGraphController {

    private final SensorGraphService service;

    public OutdoorSensorGraphController(@Qualifier("outdoorGraphService") SensorGraphService service) {
        this.service = service;
    }

    @GetMapping("/{measuredValue}/{hours}")
    public ResponseEntity<GraphView> getOutdoorTemperatureGraph(@PathVariable MeasuredValue measuredValue, @PathVariable int hours) {
        final Duration duration = Duration.ofHours(hours);
        final GraphView graph = service.getGraphView(measuredValue, duration);
        return generateResponse(graph);
    }
}
