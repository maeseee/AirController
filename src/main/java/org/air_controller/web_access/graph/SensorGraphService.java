package org.air_controller.web_access.graph;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class SensorGraphService {

    protected ResponseEntity<GraphView> generateResponse(GraphView graph) {
        if (graph.items().isEmpty()) {
            new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(graph, HttpStatus.OK);
    }
}
