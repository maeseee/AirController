package org.air_controller.web_access.graph;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
class SystemGraphService {
    private static final int MAX_NUMBER_OF_ITEMS = 150;

    private final SystemActionDbAccessor airFlowDbAccessor;

    public SystemGraphService(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    public GraphView getAirflowGraphValues(Duration duration) {
        final List<SystemAction> dataPoints = airFlowDbAccessor.getActions(duration);
        final GraphView indoorGraph = createGraphView(dataPoints);
        log.info("Asking for system fresh air status graph for a duration of {}. Returning a total of {} items", duration,
                indoorGraph.items().size());
        return indoorGraph;
    }

    private GraphView createGraphView(List<SystemAction> dataPoints) {
        final List<GraphItem> items = dataPoints.stream()
                .map(dataPoint -> new GraphItem(
                        TimeUtils.toLocalDateTime(dataPoint.actionTime()),
                        dataPoint.outputState().isOn() ? 1.0 : 0.0))
                .toList();
        return new GraphView("Air flow ON/OFF", ItemReducer.reduceTo(items, MAX_NUMBER_OF_ITEMS));
    }
}
