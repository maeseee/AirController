package org.air_controller.web_access.graph;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
class SystemGraphService {
    private final SystemActionDbAccessor airFlowDbAccessor;

    public SystemGraphService(SystemActionDbAccessor airFlowDbAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
    }

    public GraphView getAirflowGraphValues(Duration duration) {
        final List<VentilationSystemPersistenceData> dataPoints = airFlowDbAccessor.getPersistenceData(duration);
        final GraphView indoorGraph = createGraphView(dataPoints);
        log.info("Asking for system fresh air status graph for a duration of {}. Returning a total of {} items", duration,
                indoorGraph.items().size());
        return indoorGraph;
    }

    private GraphView createGraphView(List<VentilationSystemPersistenceData> persistenceData) {
        final List<GraphItem> items = persistenceData.stream()
                .map(dataPoint -> new GraphItem(
                        TimeUtils.toLocalDateTime(dataPoint.timestamp()),
                        dataPoint.totalConfidence(),
                        GraphItem.toColorString(dataPoint.action().isOn() ? Color.GREEN : Color.RED)))
                .toList();
        final DynamicItemReducer itemReducer = new DynamicItemReducer(DynamicItemReducer.MAX_NUMBER_OF_ITEMS);
        return new GraphView("Air flow ON/OFF", itemReducer.reduce(items));
    }
}
