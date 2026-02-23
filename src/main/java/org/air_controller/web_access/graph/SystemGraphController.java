package org.air_controller.web_access.graph;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class SystemGraphController {

    private final SystemGraphService service;

    @GetMapping("/graph/system/airflow/{hours}")
    public ResponseEntity<GraphView> getSystemGraph(@PathVariable int hours) {
        final Duration duration = Duration.ofHours(hours);
        final GraphView graph = service.getAirflowGraphValues(duration);
        return generateResponse(graph);
    }

    private ResponseEntity<GraphView> generateResponse(GraphView graph) {
        if (graph.items().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(graph, HttpStatus.OK);
    }
}
