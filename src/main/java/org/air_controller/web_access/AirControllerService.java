package org.air_controller.web_access;

import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.web_access.graph.GraphItem;
import org.air_controller.web_access.graph.GraphView;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class AirControllerService {

    private static final Duration INFO_DURATION = Duration.ofMinutes(10);

    private final SystemActionDbAccessor airFlowDbAccessor;
    private final ClimateDataPointsDbAccessor indoorDataPointsAccessor;

    public AirControllerService(SystemActionDbAccessor airFlowDbAccessor, ClimateDataPointsDbAccessor indoorDataPointsAccessor) {
        this.airFlowDbAccessor = airFlowDbAccessor;
        this.indoorDataPointsAccessor = indoorDataPointsAccessor;
    }

    public Optional<SystemAction> getCurrentStateForFreshAir() {
        return airFlowDbAccessor.getMostCurrentSystemAction();
    }

    public GraphView getIndoorTemperatureGraph() {
        final List<ClimateDataPoint> dataPoints = indoorDataPointsAccessor.getDataPointsFromLast24Hours();
        final List<GraphItem> temperatureItems = dataPoints.stream()
                .map(dataPoint -> new GraphItem(
                        dataPoint.timestamp(),
                        dataPoint.temperature().celsius()
                ))
                .toList();
        return new GraphView("Temperature (°C)", temperatureItems);
    }
}
