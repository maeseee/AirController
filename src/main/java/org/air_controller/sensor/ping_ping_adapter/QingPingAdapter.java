package org.air_controller.sensor.ping_ping_adapter;

import lombok.extern.slf4j.Slf4j;
import org.air_controller.sensor.ClimateSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class QingPingAdapter extends ClimateSensor {
    private final SensorReducer sensorReducer;
    private final ListDevicesJsonParser parser;

    public QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor sensor) {
        this(persistence, sensor, new SensorReducer(), new ListDevicesJsonParser());
    }

    QingPingAdapter(ClimateDataPointPersistence persistence, QingPingSensor sensor, SensorReducer sensorReducer, ListDevicesJsonParser parser) {
        super(persistence, sensor);
        this.sensorReducer = sensorReducer;
        this.parser = parser;
    }

    @Override
    protected Optional<ClimateDataPoint> parseResponse(String response) {
        final List<ClimateDataPoint> dataPoints = parse(response);
        return sensorReducer.reduce(dataPoints);
    }

    @Override
    protected String sensorType() {
        return "QingPing";
    }

    private List<ClimateDataPoint> parse(String response) {
        final List<ClimateDataPoint> dataPoints = new ArrayList<>();
        QingPingSensor.getDeviceList().forEach(
                mac -> parser.parseDeviceListResponse(response, mac).ifPresent(dataPoints::add));
        if (dataPoints.isEmpty()) {
            log.error("No sensor data found in the response: {}", response);
        }
        return dataPoints;
    }
}
