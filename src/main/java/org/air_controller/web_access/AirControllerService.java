package org.air_controller.web_access;

import org.air_controller.persistence.MariaDatabase;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_values.CarbonDioxide;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.CurrentClimateDataPoint;
import org.air_controller.system_action.SystemAction;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
import org.air_controller.system_action.VentilationSystemPersistenceData;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.INDOOR_TABLE_NAME;
import static org.air_controller.sensor_data_persistence.ClimateDataPointPersistence.OUTDOOR_TABLE_NAME;

@Service
public class AirControllerService {

    private final SystemActionDbAccessor airFlowDbAccessor = new SystemActionDbAccessor(new MariaDatabase(), SystemPart.AIR_FLOW);
    private final ClimateDataPointsDbAccessor indoorDataPointsAccessor = new ClimateDataPointsDbAccessor(new MariaDatabase(), INDOOR_TABLE_NAME);
    private final ClimateDataPointsDbAccessor outdoorDataPointsAccessor = new ClimateDataPointsDbAccessor(new MariaDatabase(), OUTDOOR_TABLE_NAME);

    public Optional<SystemAction> getCurrentStateForFreshAir() {
        return airFlowDbAccessor.getMostCurrentSystemAction();
    }

    public Optional<ClimateDataPointDTO> getCurrentIndoorClimateDataPoint() {
        final CurrentClimateDataPoint currentClimateDataPoint = new CurrentClimateDataPoint(indoorDataPointsAccessor);
        final Optional<ClimateDataPoint> dataPointOptional = currentClimateDataPoint.getCurrentClimateDataPoint();
        return mapToClimateDataPointDTO(dataPointOptional);
    }

    public Optional<ClimateDataPointDTO> getCurrentOutdoorClimateDataPoint() {
        // TODO extract function
        final CurrentClimateDataPoint currentClimateDataPoint = new CurrentClimateDataPoint(outdoorDataPointsAccessor);
        final Optional<ClimateDataPoint> dataPointOptional = currentClimateDataPoint.getCurrentClimateDataPoint();
        return mapToClimateDataPointDTO(dataPointOptional);
    }

    public Optional<Double> getCurrentTotalConfidence() {
        return airFlowDbAccessor.getMostCurrentPersistenceData().map(VentilationSystemPersistenceData::totalConfidence);
    }

    public Optional<Map<String, Double>> getCurrentConfidences() {
        return airFlowDbAccessor.getMostCurrentPersistenceData().map(VentilationSystemPersistenceData::confidences);
    }

    private Optional<ClimateDataPointDTO> mapToClimateDataPointDTO(Optional<ClimateDataPoint> dataPointOptional) {
        return dataPointOptional.map(dataPoint -> {
            final double celsius = dataPoint.temperature().celsius();
            final double relativeHumidity = dataPoint.humidity().getRelativeHumidity(dataPoint.temperature());
            final Double co2 = dataPoint.co2().map(CarbonDioxide::ppm).orElse(null);
            return new ClimateDataPointDTO(celsius, relativeHumidity, co2);
        });
    }
}
